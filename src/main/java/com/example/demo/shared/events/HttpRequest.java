package com.example.demo.shared.events;

import java.util.List;
import java.util.Map;

public record HttpRequest(String protocol,                  // "REST" (or "SOAP" later)
                       String method,                 // "GET","POST",…
                       String url,                       // absolute; or use config.baseUrl + relativePath
                       Map<String,String> headers,       // test-specified headers (non-secret)
                       byte[] body                      // request payload (small; big payloads -> artifact store)
                           ) {
}
