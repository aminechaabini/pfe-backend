package com.example.demo.Runner.validator;

import com.example.demo.shared.valueobject.AssertionResult;
import com.example.demo.shared.valueobject.AssertionSpec;

import java.net.http.HttpResponse;
import java.util.*;

/**
 * Composite validator that delegates to specific validators based on assertion type.
 * Automatically routes assertions to the appropriate validator.
 */
public class CompositeAssertionValidator implements AssertionValidator {

    private final Map<String, Validator> validators = new HashMap<>();

    /**
     * Create composite validator with multiple specific validators.
     *
     * @param validators array of specific validators
     */
    public CompositeAssertionValidator(Validator... validators) {
        for (Validator validator : validators) {
            for (String type : validator.supportedTypes()) {
                this.validators.put(type, validator);
            }
        }
    }

    @Override
    public List<AssertionResult> validate(List<AssertionSpec> assertions, HttpResponse<String> response) {
        List<AssertionResult> results = new ArrayList<>();

        for (AssertionSpec assertion : assertions) {
            Validator validator = validators.get(assertion.type());

            if (validator == null) {
                results.add(new AssertionResult(
                    assertion,
                    false,
                    "Unknown assertion type: " + assertion.type()
                ));
                continue;
            }

            AssertionResult result = validator.validate(assertion, response);
            results.add(result);
        }

        return results;
    }
}
