/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.spring.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.spring.annotated.ConsoleAgendaEventListener;
import org.kie.spring.beans.AnnotatedExampleBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class KieSpringAnnotationsInjectionTest {

    static ApplicationContext context = null;

    @BeforeClass
    public static void setup() {
        context = new ClassPathXmlApplicationContext("org/kie/spring/annotations/kie-spring-annotations.xml");
    }

    @Test
    public void testContext() throws Exception {
        assertNotNull(context);
    }

    @Test
    public void testKieBase() throws Exception {
        KieBase kbase = (KieBase) context.getBean("drl_kiesample3");
        assertNotNull(kbase);
        AnnotatedExampleBean sampleBean = (AnnotatedExampleBean) context.getBean("sampleBean");
        assertNotNull(sampleBean);
        assertNotNull(sampleBean.getKieBase() );
        assertTrue(sampleBean.getKieBase() instanceof KieBase );
    }

    @Test
    public void testSetterKieBase() throws Exception {
        AnnotatedExampleBean sampleBean = (AnnotatedExampleBean) context.getBean("sampleBean");
        assertNotNull(sampleBean);
        assertNotNull(sampleBean.getKieBase2() );
        assertTrue(sampleBean.getKieBase2() instanceof KieBase );
    }

//    @Test
//    public void testResourceKieSession() throws Exception {
//        AnnotatedExampleBean sampleBean = (AnnotatedExampleBean) context.getBean("sampleBean");
//        assertNotNull(sampleBean);
//        assertNotNull(sampleBean.getSession() );
//        assertTrue(sampleBean.getSession() instanceof StatelessKieSession);
//    }

    @Test
    public void testStatelessKSessionInjection() throws Exception {
        AnnotatedExampleBean sampleBean = (AnnotatedExampleBean) context.getBean("sampleBean");
        assertNotNull(sampleBean);
        assertNotNull(sampleBean.getKieSession() );
        assertTrue(sampleBean.getKieSession() instanceof StatelessKieSession);
    }

    @Test
    public void testStatefulKSessionInjection() throws Exception {
        AnnotatedExampleBean sampleBean = (AnnotatedExampleBean) context.getBean("sampleBean");
        assertNotNull(sampleBean);
        assertNotNull(sampleBean.getStatefulSession() );
        assertTrue(sampleBean.getStatefulSession() instanceof KieSession);
    }

    @AfterClass
    public static void tearDown() {

    }

}
