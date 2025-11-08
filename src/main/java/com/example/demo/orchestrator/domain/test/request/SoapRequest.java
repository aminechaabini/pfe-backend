package com.example.demo.orchestrator.domain.test.request;

import com.example.demo.orchestrator.domain.test.request.body.XmlBody;

import java.util.Map;

public class SoapRequest extends HttpRequest<XmlBody>{

   private Map<String, String> envelopeNamespaces;

   private Map<String, Object> soapHeaders;

   private Map<String, Object> soapBody;
}
