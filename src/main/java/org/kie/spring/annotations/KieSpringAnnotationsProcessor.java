package org.kie.spring.annotations;

import org.kie.api.KieBase;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.WorkingMemoryEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.spring.KieObjectsResolver;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class KieSpringAnnotationsProcessor implements InstantiationAwareBeanPostProcessor,
        MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware, Serializable {

    private transient final Map<Class<?>, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<Class<?>, InjectionMetadata>();
    private transient final Map<Class<?>, InjectionMetadata> wiredMetadataCache = new ConcurrentHashMap<Class<?>, InjectionMetadata>();
    private int order = Ordered.LOWEST_PRECEDENCE - 4;
    private transient ListableBeanFactory beanFactory;

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            this.beanFactory = (ListableBeanFactory) beanFactory;
        }
    }

    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class beanType, String beanName) {
        if (beanType != null) {
            InjectionMetadata metadata = findAnnotationMetadata(beanType);
            metadata.checkConfigMembers(beanDefinition);

            metadata = findAutoWiredMetadata(beanType);
            metadata.checkConfigMembers(beanDefinition);
        }
    }

    public Object postProcessBeforeInstantiation(Class beanClass, String beanName) throws BeansException {
        return null;
    }

    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    public PropertyValues postProcessPropertyValues(
            PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {

        InjectionMetadata metadata = findAnnotationMetadata(bean.getClass());
        try {
            metadata.inject(bean, beanName, pvs);
        }
        catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of kie dependencies failed", ex);
        }

        metadata = findAutoWiredMetadata(bean.getClass());
        try {
            metadata.inject(bean, beanName, pvs);
        }
        catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Wiring of kie dependencies failed", ex);
        }
        return pvs;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private InjectionMetadata findAnnotationMetadata(final Class clazz) {
        // Quick check on the concurrent map first, with minimal locking.
        InjectionMetadata metadata = this.injectionMetadataCache.get(clazz);
        if (metadata == null) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(clazz);
                if (metadata == null) {
                    LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
                    Class<?> targetClass = clazz;

                    do {
                        LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<InjectionMetadata.InjectedElement>();
                        checkForFieldInjections(targetClass, currElements);
                        checkForMethodInjections(targetClass, currElements);
                        elements.addAll(0, currElements);
                        targetClass = targetClass.getSuperclass();
                    }
                    while (targetClass != null && targetClass != Object.class);

                    metadata = new InjectionMetadata(clazz, elements);
                    this.injectionMetadataCache.put(clazz, metadata);
                }
            }
        }
        return metadata;
    }

    private InjectionMetadata findAutoWiredMetadata(final Class clazz) {
        // Quick check on the concurrent map first, with minimal locking.
        InjectionMetadata metadata = this.wiredMetadataCache.get(clazz);
        if (metadata == null) {
            synchronized (this.wiredMetadataCache) {
                metadata = this.wiredMetadataCache.get(clazz);
                if (metadata == null) {
                    LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
                    Class<?> targetClass = clazz;

                    do {
                        LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<InjectionMetadata.InjectedElement>();
                        checkForTypeMetadata(targetClass, currElements);
                        elements.addAll(0, currElements);
                        targetClass = targetClass.getSuperclass();
                    }
                    while (targetClass != null && targetClass != Object.class);

                    metadata = new InjectionMetadata(clazz, elements);
                    this.wiredMetadataCache.put(clazz, metadata);
                }
            }
        }
        return metadata;
    }

    private void checkForTypeMetadata(Class<?> targetClass, LinkedList<InjectionMetadata.InjectedElement> currElements) {
        KListener listener = targetClass.getAnnotation(KListener.class);
        if ( listener != null ) {
            Member member = targetClass.getConstructors()[0];
            currElements.add(new KListenerWiredElement(member, listener, null));
        }
    }

    private void checkForMethodInjections(Class<?> targetClass, LinkedList<InjectionMetadata.InjectedElement> currElements) {
        for (Method method : targetClass.getDeclaredMethods()) {
            KSession kSession = method.getAnnotation(KSession.class);
            KBase kBase = method.getAnnotation(KBase.class);
            if ((kSession != null || kBase != null) &&
                    method.equals(ClassUtils.getMostSpecificMethod(method, targetClass))) {
                if (Modifier.isStatic(method.getModifiers())) {
                    throw new IllegalStateException("Kie Annotations are not supported on static methods");
                }
                if (method.getParameterTypes().length != 1) {
                    throw new IllegalStateException("Kie Annotation requires a single-arg method: " + method);
                }
                PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
                if ( kSession != null ) {
                    currElements.add(new KSessionInjectedElement(method, pd));
                } else if (kBase != null ) {
                    currElements.add(new KBaseInjectedElement(method, pd));
                }
            }
        }
    }

    private void checkForFieldInjections(Class<?> targetClass, LinkedList<InjectionMetadata.InjectedElement> currElements) {
        for (Field field : targetClass.getDeclaredFields()) {
            KBase kBase = field.getAnnotation(KBase.class);
            if (kBase != null) {
                if (Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalStateException("Kie Annotations are not supported on static fields");
                }
                currElements.add(new KBaseInjectedElement(field, null));
            }
            KSession kSession = field.getAnnotation(KSession.class);
            if (kSession != null) {
                if (Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalStateException("Kie Annotations are not supported on static fields");
                }
                currElements.add(new KSessionInjectedElement(field, null));
            }
        }
    }

    private class KieElementInjectedElement extends InjectionMetadata.InjectedElement {
        protected String name;
        public KieElementInjectedElement(Member member, PropertyDescriptor pd) {
            super(member, pd);
        }

        protected Object getResourceToInject(Object target, String requestingBeanName) {
            return beanFactory.getBean(name);
        }
    }

    private class KBaseInjectedElement extends KieElementInjectedElement {

        public KBaseInjectedElement(Member member, PropertyDescriptor pd) {
            super(member, pd);
            AnnotatedElement ae = (AnnotatedElement) member;
            KBase aeAnnotation = ae.getAnnotation(KBase.class);
            name = aeAnnotation.name();
            checkResourceType(KieBase.class);
        }

    }

    private class KSessionInjectedElement extends KieElementInjectedElement {

        String type;
        public KSessionInjectedElement(Member member, PropertyDescriptor pd) {
            super(member, pd);
            AnnotatedElement ae = (AnnotatedElement) member;
            KSession kSessionAnnotation = ae.getAnnotation(KSession.class);
            type = kSessionAnnotation.type();
            name = kSessionAnnotation.name();

            if ( "stateless".equalsIgnoreCase(type)){
                checkResourceType(StatelessKieSession.class);
            } else {
                checkResourceType(KieSession.class);
            }
        }
    }

    private class KListenerWiredElement extends KieElementInjectedElement {

        KListener.LISTENER_TYPE type;
        public KListenerWiredElement(Member member, KListener annotation, PropertyDescriptor pd) {
            super(member, pd);
            name = annotation.ksession();
            type = annotation.type();
        }

        protected void inject(Object target, String requestingBeanName, PropertyValues pvs) throws Throwable {
            KieRuntimeEventManager kieRuntimeEventManager = (KieRuntimeEventManager) beanFactory.getBean(name);
            if ( target instanceof AgendaEventListener &&
                    (type == KListener.LISTENER_TYPE.DERIVE || type == KListener.LISTENER_TYPE.AGENDA)) {
                kieRuntimeEventManager.addEventListener((AgendaEventListener)target);
                System.out.println("********* Adding Agenda Listener to "+name+"  ( Listener - "+target+") ************** ");
            }
            if ( target instanceof WorkingMemoryEventListener  &&
                    (type == KListener.LISTENER_TYPE.DERIVE || type == KListener.LISTENER_TYPE.WORKING_MEMORY)) {
                kieRuntimeEventManager.addEventListener((WorkingMemoryEventListener)target);
                System.out.println("********* Adding WorkingMemory Listener to " + name + "  ( Listener - " + target + ") ************** ");
            }
            if ( target instanceof ProcessEventListener  &&
                    (type == KListener.LISTENER_TYPE.DERIVE || type == KListener.LISTENER_TYPE.PROCESS_EVENT)) {
                kieRuntimeEventManager.addEventListener((ProcessEventListener)target);
                System.out.println("********* Adding ProcessEvent Listener to " + name + "  ( Listener - " + target + ") ************** ");
            }
        }

    }
}