package org.kie.spring.annotations;

import org.kie.api.builder.ReleaseId;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.util.LinkedHashSet;
import java.util.Set;

class KieAnnotationsUtil {

    private static final String KIE_ANNOTATION_PROCESSOR_CLASS_NAME =
            "org.kie.spring.annotations.KieSpringAnnotationsProcessor";


    /**
     * Register all relevant annotation post processors in the given registry.
     * @param registry the registry to operate on
     */
    public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry, ReleaseId releaseId) {
        registerAnnotationConfigProcessors(registry, null, releaseId);
    }

    /**
     * Register all relevant annotation post processors in the given registry.
     * @param registry the registry to operate on
     * @param source the configuration source element (already extracted)
     * that this registration was triggered from. May be <code>null</code>.
     * @return a Set of BeanDefinitionHolders, containing all bean definitions
     * that have actually been registered by this call
     */
    public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(
            BeanDefinitionRegistry registry, Object source, ReleaseId releaseId) {

        Set<BeanDefinitionHolder> beanDefs = new LinkedHashSet<BeanDefinitionHolder>(1);

        if (!registry.containsBeanDefinition(KIE_ANNOTATION_PROCESSOR_CLASS_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(KieSpringAnnotationsProcessor.class);
            def.setSource(source);
            def.getPropertyValues().add("releaseId", releaseId);
            beanDefs.add(registerPostProcessor(registry, def, KIE_ANNOTATION_PROCESSOR_CLASS_NAME));
        }

        return beanDefs;
    }

    private static BeanDefinitionHolder registerPostProcessor(
            BeanDefinitionRegistry registry, RootBeanDefinition definition, String beanName) {

        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(beanName, definition);
        return new BeanDefinitionHolder(definition, beanName);
    }
}
