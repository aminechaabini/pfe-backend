package com.example.demo.llm_adapter;


import com.example.demo.shared.events.RunRequest;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface
AIServiceThing {

    @SystemMessage("You are an API test generation engine. Do not include commentary.\n")
    public RunRequest getRunRequest(@UserMessage String input);
}
