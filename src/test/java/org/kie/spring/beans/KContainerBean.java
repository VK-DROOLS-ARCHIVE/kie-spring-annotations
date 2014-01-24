package org.kie.spring.beans;

import org.kie.api.cdi.KContainer;
import org.kie.api.runtime.KieContainer;

public class KContainerBean {

    @KContainer
    KieContainer kieContainer;

    KieContainer kieContainer2;

    public KieContainer getKieContainer() {
        return kieContainer;
    }

    public void setKieContainer(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public KieContainer getKieContainer2() {
        return kieContainer2;
    }

    @KContainer
    public void setKieContainer2(KieContainer kieContainer2) {
        this.kieContainer2 = kieContainer2;
    }
}
