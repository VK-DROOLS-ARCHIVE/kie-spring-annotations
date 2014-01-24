package org.kie.spring.beans;

import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

public class DefaultValuesBean {

    @KBase
    KieBase kieBase;

    KieBase kieBase2;

    public KieBase getKieBase() {
        return kieBase;
    }
    public void setKieBase(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    public KieBase getKieBase2() {
        return kieBase2;
    }

    @KBase
    public void setKieBase2(KieBase kieBase2) {
        this.kieBase2 = kieBase2;
    }
}
