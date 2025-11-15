package com.example.demo.orchestrator.domain.spec;

public class RestEndpoint extends Endpoint {
    // REST-specific fields
    public HttpMethod method; // GET, POST, etc.
    public String path;       // /api/orders/{id}

    // Implementations from spec
    @Override
    public String getDisplayName() {
        return method + " " + path;
    }

    @Override
    public String getUniqueKey() {
        return method + ":" + path;
    }

    @Override
    public EndpointType getType() {
        return EndpointType.REST;
    }

    @Override
    public boolean hasPathParameters() {
        return path != null && path.contains("{");
    }

    // REST-specific methods (minimal implementations)
    public String getResourceName() {
        if (path == null || path.isEmpty()) return null;
        // return the last non-template path segment (e.g. /api/orders/{id} -> orders)
        String[] parts = path.split("/");
        for (int i = parts.length - 1; i >= 0; i--) {
            String p = parts[i];
            if (p.isEmpty()) continue;
            if (p.startsWith("{") && p.endsWith("}")) continue;
            return p;
        }
        return null;
    }

    public boolean isReadOnly() {
        return method == HttpMethod.GET || method == HttpMethod.HEAD;
    }

    public boolean isMutating() {
        return !isReadOnly();
    }
}