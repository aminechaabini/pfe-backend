package com.example.demo.shared.request;

/**
 * Base interface for API run requests (REST and SOAP).
 */
public sealed interface ApiRunRequest extends RunRequest permits RestRunRequest, SoapRunRequest {
}
