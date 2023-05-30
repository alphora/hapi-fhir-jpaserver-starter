package org.opencds.cqf.jpa.starter.common;

import ca.uhn.fhir.jpa.config.r4.JpaR4Config;
import org.opencds.cqf.jpa.starter.annotations.OnR4Condition;
import org.opencds.cqf.jpa.starter.cr.StarterCrR4Config;
import org.opencds.cqf.jpa.starter.ips.StarterIpsConfig;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Conditional(OnR4Condition.class)
@Import({
	JpaR4Config.class,
	StarterJpaConfig.class,
	StarterCrR4Config.class,
	ElasticsearchConfig.class,
	StarterIpsConfig.class
})
public class FhirServerConfigR4 {
}
