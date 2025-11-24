package com.example.demo.common.dto.ai;

import com.example.demo.common.context.dto.analysis.RestSpecUpdateAnalysis;
import com.example.demo.common.context.dto.analysis.SoapSpecUpdateAnalysis;

/**
 * Result wrapper for spec update analysis from AI.
 * Can contain either REST or SOAP analysis.
 */
public record SpecUpdateAnalysisResult(
    RestSpecUpdateAnalysis restAnalysis,
    SoapSpecUpdateAnalysis soapAnalysis
) {
    public SpecUpdateAnalysisResult {
        if (restAnalysis == null && soapAnalysis == null) {
            throw new IllegalArgumentException("Either REST or SOAP analysis must be provided");
        }
        if (restAnalysis != null && soapAnalysis != null) {
            throw new IllegalArgumentException("Cannot have both REST and SOAP analysis");
        }
    }

    public static SpecUpdateAnalysisResult forRest(RestSpecUpdateAnalysis analysis) {
        return new SpecUpdateAnalysisResult(analysis, null);
    }

    public static SpecUpdateAnalysisResult forSoap(SoapSpecUpdateAnalysis analysis) {
        return new SpecUpdateAnalysisResult(null, analysis);
    }

    public boolean isRest() {
        return restAnalysis != null;
    }

    public boolean isSoap() {
        return soapAnalysis != null;
    }
}
