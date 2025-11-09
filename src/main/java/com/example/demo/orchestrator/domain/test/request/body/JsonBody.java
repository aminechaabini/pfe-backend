package com.example.demo.orchestrator.domain.test.request.body;

import com.eclipsesource.json.JsonObject;

import java.util.Objects;

/**
 * JSON body for HTTP requests.
 */
public class JsonBody implements Body {

    private JsonObject jsonObject;

    public JsonBody() {
        this.jsonObject = new JsonObject();
    }

    public JsonBody(JsonObject jsonObject) {
        this.jsonObject = Objects.requireNonNull(jsonObject, "JSON object cannot be null");
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = Objects.requireNonNull(jsonObject, "JSON object cannot be null");
    }

    /**
     * Get the JSON as a string.
     */
    public String toJsonString() {
        return jsonObject != null ? jsonObject.toString() : "{}";
    }
}
