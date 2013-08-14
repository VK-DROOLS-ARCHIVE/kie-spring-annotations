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

import static org.junit.Assert.*;

public class KieSpringAnnotationsWiringTest {

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
    public void testAutoWiredAgendaListener() throws Exception {
        AnnotatedExampleBean sampleBean = (AnnotatedExampleBean) context.getBean("sampleBean");
        assertNotNull(sampleBean);
        assertNotNull(sampleBean.getKieSession() );
        assertTrue(sampleBean.getKieSession() instanceof StatelessKieSession);

        Collection<AgendaEventListener> agendaEventListeners = sampleBean.getKieSession().getAgendaEventListeners();
        assertTrue(agendaEventListeners.size() > 0);
        for (AgendaEventListener listener : agendaEventListeners ) {
            if (listener instanceof ConsoleAgendaEventListener) {
                return;
            }
        }
        fail();
    }


    @AfterClass
    public static void tearDown() {

    }

}
