package org.kie.spring.annotated;

import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.spring.annotations.KListener;

@KListener(ksession="ksession1")
public class ConsoleAgendaEventListener extends DebugAgendaEventListener {
}
