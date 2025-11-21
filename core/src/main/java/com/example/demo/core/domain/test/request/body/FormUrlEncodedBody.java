package com.example.demo.core.domain.test.request.body;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FormUrlEncodedBody implements Body{

    private Map<String, List<String>> params = new LinkedHashMap<>();

}
