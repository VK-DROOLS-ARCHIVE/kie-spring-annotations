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

package org.kie.spring.annotations.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.spring.beans.KContainerBean;
import org.kie.spring.beans.NamedKieBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class KContainerInjectionTest {

    static ApplicationContext context = null;

    @BeforeClass
    public static void setup() {
        context = new ClassPathXmlApplicationContext("org/kie/spring/annotations/kcontainer-tests.xml");
    }

    @Test
    public void testContext() throws Exception {
        assertNotNull(context);
    }

    @Test
    public void testKContainer() throws Exception {
        KContainerBean sampleBean = (KContainerBean) context.getBean("sampleBean");
        assertNotNull(sampleBean);
        assertNotNull(sampleBean.getKieContainer() );
        assertTrue(sampleBean.getKieContainer() instanceof KieContainer );
        ReleaseId releaseId = sampleBean.getKieContainer().getReleaseId();
        assertTrue("kie-spring-annotations".equalsIgnoreCase(releaseId.getArtifactId()));
    }

    @Test
    public void testSetterKContainer() throws Exception {
        KContainerBean sampleBean = (KContainerBean) context.getBean("sampleBean");
        assertNotNull(sampleBean);
        assertNotNull(sampleBean.getKieContainer2());
        assertTrue(sampleBean.getKieContainer2() instanceof KieContainer);
        ReleaseId releaseId = sampleBean.getKieContainer2().getReleaseId();
        assertTrue("kie-spring-annotations".equalsIgnoreCase(releaseId.getArtifactId()));
    }


    @AfterClass
    public static void tearDown() {

    }

}
