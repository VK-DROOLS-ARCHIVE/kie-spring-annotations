package org.kie.spring.beans;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.spring.annotations.KBase;
import org.kie.spring.annotations.KSession;

public class AnnotatedExampleBean {

    @KSession(name = "ksession1", type = "stateless")
    StatelessKieSession ksession1;

    @KSession(name="ksession2")
    KieSession statefulSession;

    @KBase(name="drl_kiesample3")
    KieBase kieBase;

    KieBase kieBase2;

    public KieBase getKieBase2() {
        return kieBase2;
    }

    @KBase(name="drl_kiesample3")
    public void setKieBase2(KieBase kieBase2) {
        this.kieBase2 = kieBase2;
    }

    public KieBase getKieBase() {
        return kieBase;
    }

    public void setKieBase(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    public KieSession getStatefulSession() {
        return statefulSession;
    }

    public void setStatefulSession(KieSession statefulSession) {
        this.statefulSession = statefulSession;
    }

    public StatelessKieSession getKieSession() {
        return ksession1;
    }

    public void setKieSession(StatelessKieSession kieSession) {
        this.ksession1 = kieSession;
    }
}
