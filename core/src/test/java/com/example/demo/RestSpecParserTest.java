package com.example.demo;

import com.example.demo.core.infrastructure.spec.RestSpecParser;
import org.junit.jupiter.api.Test;

public class RestSpecParserTest {

    @Test
    void testparser(){
        RestSpecParser parser = new RestSpecParser();
        parser.parse("test");
    }
}
