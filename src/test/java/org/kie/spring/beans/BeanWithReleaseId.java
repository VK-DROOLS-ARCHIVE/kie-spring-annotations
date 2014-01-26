package org.kie.spring.beans;

import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.cdi.KContainer;
import org.kie.api.cdi.KReleaseId;
import org.kie.api.runtime.KieContainer;

public class BeanWithReleaseId {

    @KReleaseId(groupId = "org.drools", artifactId = "named-kiesession", version = "6.1.0-SNAPSHOT")
    @KBase("kbase1")
    KieBase kieBase;

    public KieBase getKieBase() {
        return kieBase;
    }
    public void setKieBase(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    @KContainer
    @KReleaseId(groupId = "org.drools", artifactId = "named-kiesession", version = "6.1.0-SNAPSHOT")
    KieContainer kieContainer;

    public KieContainer getKieContainer() {
        return kieContainer;
    }

    public void setKieContainer(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }
}
