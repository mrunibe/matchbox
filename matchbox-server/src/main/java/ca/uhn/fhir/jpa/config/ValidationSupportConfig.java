/*-
 * #%L
 * HAPI FHIR JPA Server
 * %%
 * Copyright (C) 2014 - 2025 Smile CDR, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package ca.uhn.fhir.jpa.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.jpa.api.config.JpaStorageSettings;
import ca.uhn.fhir.jpa.validation.JpaValidationSupportChain;
import ca.uhn.fhir.jpa.validation.FhirContextValidationSupportSvc;
import ca.uhn.fhir.jpa.validation.ValidatorPolicyAdvisor;
import ca.uhn.fhir.jpa.validation.ValidatorResourceFetcher;
import ca.uhn.fhir.validation.IInstanceValidatorModule;
import ca.uhn.fhir.validation.IValidationContext;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

@Configuration
public class ValidationSupportConfig {
  
  public class DummyInstanceValidatorModule implements IInstanceValidatorModule {

    @Override
    public void validateResource(IValidationContext<IBaseResource> theCtx) {
    }

  }

  public class DummyValidationSupport implements IValidationSupport {

    FhirContext context;

    DummyValidationSupport(FhirContext context) {
      this.context = context;
    }

    @Override
    public FhirContext getFhirContext() {
      return context;
    }
  }

	@Autowired
	private FhirContext myFhirContext;

	@Bean
	public FhirContextValidationSupportSvc fhirValidationSupportSvc() {
		return new FhirContextValidationSupportSvc(myFhirContext);
	}

	@Bean
	public InMemoryTerminologyServerValidationSupport inMemoryTerminologyServerValidationSupport(
			FhirContext theFhirContext, JpaStorageSettings theStorageSettings) {
		InMemoryTerminologyServerValidationSupport retVal =
				new InMemoryTerminologyServerValidationSupport(theFhirContext);
		retVal.setIssueSeverityForCodeDisplayMismatch(theStorageSettings.getIssueSeverityForCodeDisplayMismatch());
		return retVal;
	}

	@Bean(name = JpaConfig.DEFAULT_PROFILE_VALIDATION_SUPPORT)
	public DefaultProfileValidationSupport defaultProfileValidationSupport(FhirContext theFhirContext) {
		return new DefaultProfileValidationSupport(theFhirContext);
	}

	@Bean(name = JpaConfig.JPA_VALIDATION_SUPPORT_CHAIN)
	public JpaValidationSupportChain jpaValidationSupportChain(FhirContext theFhirContext) {
		return new JpaValidationSupportChain(theFhirContext);
	}

	@Bean(name = JpaConfig.JPA_VALIDATION_SUPPORT)
	public IValidationSupport jpaValidationSupport(FhirContext theFhirContext) {
		return new DummyValidationSupport(theFhirContext);
	}

	@Bean(name = "myInstanceValidator")
	public IInstanceValidatorModule instanceValidator() {
    return new DummyInstanceValidatorModule();
	}

	@Bean
	@Lazy
	public ValidatorResourceFetcher jpaValidatorResourceFetcher() {
    return new ValidatorResourceFetcher();
	}

	@Bean
	@Lazy
	public ValidatorPolicyAdvisor jpaValidatorPolicyAdvisor() {
		return new ValidatorPolicyAdvisor();
	}
}