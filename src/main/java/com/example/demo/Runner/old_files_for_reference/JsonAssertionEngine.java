package com.example.demo.Runner.old_files_for_reference;

import com.example.demo.shared.events.AssertionResult;
import com.example.demo.shared.events.AssertionSpec;
import org.assertj.core.api.Assertions;
import java.util.ArrayList;
import java.util.List;

import static net.javacrumbs.jsonunit.JsonAssert.*;


public class JsonAssertionEngine {

    public static List<AssertionResult> runAll(
            List<AssertionSpec> specs,
            String jsonBody,
            int httpStatus
    ){
        List<AssertionResult> out = new ArrayList<>();
        for (AssertionSpec s : specs) {

            try {
                switch (s.type()){
                    case "statusEquals" -> {
                        int expected = Integer.parseInt(s.expected());
                        Assertions.assertThat(httpStatus).isEqualTo(expected);
                }
                    case "jsonPathExists" -> assertJsonNodePresent(jsonBody, s.expr());
                    case "jsonPathEquals" -> assertJsonPartEquals(s.expected(), jsonBody, s.expr());
                    default -> throw new IllegalArgumentException("Unknown assertion type: " + s.type());
                }
                out.add(new AssertionResult(s, true, "OK"));
            } catch (AssertionError | RuntimeException e) {
                out.add(new AssertionResult(s, false, e.getMessage()));
            }

        }
        return out;
    }
}
