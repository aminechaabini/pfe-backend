package com.example.demo.shared.events;

import java.util.List;
import java.util.Map;

public record TestSpec(String protocol,                  // "REST" (or "SOAP" later)
                       String operation,                 // "GET","POST",…
                       String url,                       // absolute; or use config.baseUrl + relativePath
                       Map<String,String> headers,       // test-specified headers (non-secret)
                       byte[] body,                      // request payload (small; big payloads -> artifact store)
                       List<AssertionSpec> assertions    ) {
}
