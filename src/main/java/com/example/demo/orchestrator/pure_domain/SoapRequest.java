package com.example.demo.orchestrator.pure_domain;

import java.util.Map;

public class SoapRequest extends Request{

    private String url;

   private Map<String, String> httpHeaders;

   private Map<String, String> enveloppeNamespaces;

   private Map<String, Object> soapHeaders;

   private Map<String, Object> soapBody;
}
