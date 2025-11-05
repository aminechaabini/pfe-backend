package com.example.demo.orchestrator.domain.test.request;

import java.util.Map;

public class SoapRequest extends HttpRequest{

    private String url;

   private Map<String, String> httpHeaders;

   private Map<String, String> envelopeNamespaces;

   private Map<String, Object> soapHeaders;

   private Map<String, Object> soapBody;
}
