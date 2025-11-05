package com.example.demo.Runner;

import com.example.demo.shared.events.HttpRequest;
import com.example.demo.shared.events.RunRequest;

public interface RequestBuilder {
    HttpRequest build(HttpRequest request);
}
