package org.opencds.cqf.jpa.starter.common;

import ca.uhn.fhir.jpa.config.dstu3.JpaDstu3Config;
import org.opencds.cqf.jpa.starter.annotations.OnDSTU3Condition;
import org.opencds.cqf.jpa.starter.cr.StarterCrDstu3Config;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Conditional(OnDSTU3Condition.class)
@Import({
	JpaDstu3Config.class,
	StarterJpaConfig.class,
	StarterCrDstu3Config.class,
	ElasticsearchConfig.class})
public class FhirServerConfigDstu3 {
}
