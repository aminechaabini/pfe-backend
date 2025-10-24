package com.example.demo.Runner.infra;

import com.example.demo.Runner.domain.Test;

import java.util.List;

public class RunRepo {
    Test load(Long id) {
        return new Test( "REST",
                "GET",
                "url",
                null,
                null,
                null);
    }
}
