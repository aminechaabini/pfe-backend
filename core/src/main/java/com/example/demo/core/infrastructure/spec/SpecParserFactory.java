package com.example.demo.core.infrastructure.spec;

import com.example.demo.core.app.service.spec.SpecParser;
import com.example.demo.core.domain.spec.SpecType;
import org.springframework.stereotype.Component;

/**
 * Factory for obtaining the appropriate SpecParser based on spec type.
 *
 * Routes to:
 * - RestSpecParser for OpenAPI 3.0, OpenAPI 3.1, Swagger 2.0
 * - SoapSpecParser for WSDL 1.1, WSDL 2.0
 */
@Component
public class SpecParserFactory {

    private final RestSpecParser restSpecParser;
    private final SoapSpecParser soapSpecParser;

    public SpecParserFactory(RestSpecParser restSpecParser,
                              SoapSpecParser soapSpecParser) {
        this.restSpecParser = restSpecParser;
        this.soapSpecParser = soapSpecParser;
    }

    /**
     * Get the appropriate parser for the given spec type.
     *
     * @param specType the type of specification
     * @return the parser that can handle this spec type
     * @throws IllegalArgumentException if spec type is not supported
     */
    public SpecParser getParser(SpecType specType) {
        if (specType.isRest()) {
            return restSpecParser;
        } else if (specType.isSoap()) {
            return soapSpecParser;
        } else {
            throw new IllegalArgumentException("Unsupported spec type: " + specType);
        }
    }
}
