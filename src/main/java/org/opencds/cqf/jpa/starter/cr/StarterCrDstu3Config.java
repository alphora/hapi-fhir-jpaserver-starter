package org.opencds.cqf.jpa.starter.cr;

import ca.uhn.fhir.cr.config.CrDstu3Config;
import org.opencds.cqf.jpa.starter.annotations.OnDSTU3Condition;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Conditional({OnDSTU3Condition.class, CrConfigCondition.class})
@Import({CrDstu3Config.class})
public class StarterCrDstu3Config {
}
