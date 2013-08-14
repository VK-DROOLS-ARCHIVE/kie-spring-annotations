package org.kie.spring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.TYPE})
@Retention(RUNTIME)
public @interface KListener {
    String ksession();
    LISTENER_TYPE type() default LISTENER_TYPE.DERIVE;
    public enum LISTENER_TYPE { DERIVE, AGENDA, WORKING_MEMORY, PROCESS_EVENT };
}
