package ca.uhn.fhir.jpa.starter.cr;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.cr.config.dstu3.CrDstu3Config;
import ca.uhn.fhir.cr.dstu3.IActivityDefinitionProcessorFactory;
import ca.uhn.fhir.cr.dstu3.IPlanDefinitionProcessorFactory;
import ca.uhn.fhir.cr.dstu3.IQuestionnaireProcessorFactory;
import ca.uhn.fhir.cr.dstu3.IQuestionnaireResponseProcessorFactory;
import ca.uhn.fhir.cr.dstu3.activitydefinition.ActivityDefinitionOperationsProvider;
import ca.uhn.fhir.cr.dstu3.plandefinition.PlanDefinitionOperationsProvider;
import ca.uhn.fhir.cr.dstu3.questionnaire.QuestionnaireOperationsProvider;
import ca.uhn.fhir.cr.dstu3.questionnaireresponse.QuestionnaireResponseOperationsProvider;
import ca.uhn.fhir.jpa.starter.AppProperties;
import ca.uhn.fhir.jpa.starter.annotations.OnDSTU3Condition;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.provider.ResourceProviderFactory;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.cqframework.cql.cql2elm.model.Model;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.fhir.cql.EvaluationSettings;
import org.opencds.cqf.fhir.cr.measure.MeasureEvaluationOptions;
import org.opencds.cqf.fhir.utility.ValidationProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.*;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Conditional({ OnDSTU3Condition.class, CrConfigCondition.class })
@Import({ CrDstu3Config.class })
public class StarterCrDstu3Config {
	private static final Logger ourLogger = LoggerFactory.getLogger(StarterCrDstu3Config.class);

	@Bean
	MeasureEvaluationOptions measureEvaluationOptions(EvaluationSettings theEvaluationSettings, Map<String, ValidationProfile> theValidationProfiles){
		MeasureEvaluationOptions measureEvalOptions = new MeasureEvaluationOptions();
		measureEvalOptions.setEvaluationSettings(theEvaluationSettings);

		if(measureEvalOptions.isValidationEnabled()) {
			measureEvalOptions.setValidationProfiles(theValidationProfiles);
		}
		return measureEvalOptions;
	}

	@Bean
	public EvaluationSettings evaluationSettings(
		AppProperties theAppProperties,
		Map<VersionedIdentifier, CompiledLibrary> theGlobalLibraryCache,
		Map<ModelIdentifier, Model> theGlobalModelCache) {
		var evaluationSettings = EvaluationSettings.getDefault();
		var cqlOptions = evaluationSettings.getCqlOptions();

		var cqlEngineOptions = cqlOptions.getCqlEngineOptions();
		Set<CqlEngine.Options> options = EnumSet.noneOf(CqlEngine.Options.class);
		if (theAppProperties.isCqlRuntimeEnableExpressionCaching()) {
			options.add(CqlEngine.Options.EnableExpressionCaching);
		}
		if (theAppProperties.isCqlRuntimeEnableValidation()) {
			options.add(CqlEngine.Options.EnableValidation);
		}
		cqlEngineOptions.setOptions(options);
		cqlOptions.setCqlEngineOptions(cqlEngineOptions);

		var cqlCompilerOptions = new CqlCompilerOptions();

		if (theAppProperties.isEnableDateRangeOptimization()
		) {
			cqlCompilerOptions.setOptions(CqlCompilerOptions.Options.EnableDateRangeOptimization);
		}
		if (theAppProperties.isEnableAnnotations()) {
			cqlCompilerOptions.setOptions(CqlCompilerOptions.Options.EnableAnnotations);
		}
		if (theAppProperties.isEnableLocators()) {
			cqlCompilerOptions.setOptions(CqlCompilerOptions.Options.EnableLocators);
		}
		if (theAppProperties.isEnableResultsType()) {
			cqlCompilerOptions.setOptions(CqlCompilerOptions.Options.EnableResultTypes);
		}
		cqlCompilerOptions.setVerifyOnly(theAppProperties.isCqlCompilerVerifyOnly());
		if (theAppProperties.isEnableDetailedErrors()) {
			cqlCompilerOptions.setOptions(CqlCompilerOptions.Options.EnableDetailedErrors);
		}
		cqlCompilerOptions.setErrorLevel(theAppProperties.getCqlCompilerErrorSeverityLevel());
		if (theAppProperties.isDisableListTraversal()) {
			cqlCompilerOptions.setOptions(CqlCompilerOptions.Options.DisableListTraversal);
		}
		if (theAppProperties.isDisableListDemotion()) {
			cqlCompilerOptions.setOptions(CqlCompilerOptions.Options.DisableListDemotion);
		}
		if (theAppProperties.isDisableListPromotion()) {
			cqlCompilerOptions.setOptions(CqlCompilerOptions.Options.DisableListPromotion);
		}
		if (theAppProperties.isEnableIntervalDemotion()) {
			cqlCompilerOptions.setOptions(CqlCompilerOptions.Options.EnableIntervalDemotion);
		}
		if (theAppProperties.isEnableIntervalPromotion()) {
			cqlCompilerOptions.setOptions(CqlCompilerOptions.Options.EnableIntervalPromotion);
		}
		if (theAppProperties.isDisableMethodInvocation()) {
			cqlCompilerOptions.setOptions(CqlCompilerOptions.Options.DisableMethodInvocation);
		}
		if (theAppProperties.isRequireFromKeyword()) {
			cqlCompilerOptions.setOptions(CqlCompilerOptions.Options.RequireFromKeyword);
		}
		cqlCompilerOptions.setValidateUnits(theAppProperties.isCqlCompilerValidateUnits());
		if (theAppProperties.isDisableDefaultModelInfoLoad()) {
			cqlCompilerOptions.setOptions(CqlCompilerOptions.Options.DisableDefaultModelInfoLoad);
		}
		cqlCompilerOptions.setSignatureLevel(theAppProperties.getCqlCompilerSignatureLevel());
		cqlCompilerOptions.setCompatibilityLevel(theAppProperties.getCqlCompilerCompatibilityLevel());
		cqlCompilerOptions.setAnalyzeDataRequirements(theAppProperties.isCqlCompilerAnalyzeDataRequirements());
		cqlCompilerOptions.setCollapseDataRequirements(theAppProperties.isCqlCompilerCollapseDataRequirements());

		cqlOptions.setCqlCompilerOptions(cqlCompilerOptions);
		evaluationSettings.setLibraryCache(theGlobalLibraryCache);
		evaluationSettings.setModelCache(theGlobalModelCache);
		return evaluationSettings;
	}

	@Bean
	public Map<VersionedIdentifier, CompiledLibrary> globalLibraryCache() {
		return new ConcurrentHashMap<>();
	}

	@Bean
	public Map<ModelIdentifier, Model> globalModelCache() {
		return new ConcurrentHashMap<>();
	}

}
