package org.kie.spring.beans;

import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.cdi.KReleaseId;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

import javax.annotation.Resource;
import javax.inject.Inject;

public class AnnotatedExampleBean {

    @KSession("ksession1")
    StatelessKieSession ksession1;

    @KSession("ksession2")
    KieSession statefulSession;

    @KBase("drl_kiesample3")
    KieBase kieBase;

    // inject the default kbase.
    @KBase
    KieBase kieBase2;

//    @Resource(name = "ksession2")
    private KieSession session;

    public KieSession getSession() {
        return session;
    }

    public void setSession(KieSession session) {
        this.session = session;
    }

    public KieBase getKieBase2() {
        return kieBase2;
    }

    @KBase("drl_kiesample3")
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
