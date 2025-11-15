package com.example.demo.llm_adapter.old;


import com.example.demo.shared.events.RunRequest;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Service;

import java.util.Set;

import static dev.langchain4j.model.chat.Capability.RESPONSE_FORMAT_JSON_SCHEMA;

@Service
public class Generator {

    OpenAiChatModel model = OpenAiChatModel.builder()
            .apiKey(API_KEY)
            .baseUrl("https://router.huggingface.co/v1")
            .modelName("meta-llama/Llama-3.1-8B-Instruct")
            .strictJsonSchema(true) // Force strict adherence to JSON schema
            .supportedCapabilities(Set.of(RESPONSE_FORMAT_JSON_SCHEMA)) // Declare support for JSON schema
            .build();

    public RunRequest getResponse(String userRequest, String context ){
        AIServiceThing aiServiceThing = AiServices.builder(AIServiceThing.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(3))
                .build();

        String combinedPrompt = "Given this context " + context + " and user request : " + userRequest + ". \n generate the appropriate api test";
        return aiServiceThing.getRunRequest(combinedPrompt);
    }

}
