package org.opencds.cqf.jpa.starter.cr;

import ca.uhn.fhir.cr.config.CrR4Config;
import org.opencds.cqf.jpa.starter.annotations.OnR4Condition;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;

@Conditional({OnR4Condition.class, CrConfigCondition.class})
@Import({CrR4Config.class})
public class StarterCrR4Config {
}
