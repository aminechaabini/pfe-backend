package com.example.demo.llm_adapter;

import com.example.demo.llm_adapter.ai_services.TestSuiteGenerator;
import com.example.demo.llm_adapter.ai_services.TestsPreviewGenerator;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatModel openAiChatModel(@Value("${ai.openai.apiKey}") String apiKey,
                                     @Value("${ai.openai.modelName:gpt-4-1}") String modelName) {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }

    // Create one bean per @AiService interface
    @Bean
    public TestsPreviewGenerator testGenerator(ChatModel chatModel) {
        return AiServices.create(TestGenerator.class, chatModel);
    }

    @Bean
    public TestSuiteGenerator failureAnalyzer(ChatModel chatModel) {
        return AiServices.create(FailureAnalyzer.class, chatModel);
    }

    @Bean
    public E2EHeadlessGenerator e2eHeadlessGenerator(ChatModel chatModel) {
        return AiServices.create(E2EHeadlessGenerator.class, chatModel);
    }

    @Bean
    public TestEditor testEditor(ChatModel chatModel) {
        return AiServices.create(TestEditor.class, chatModel);
    }

    // optional: group beans into a Map if you want dynamic selection by name
    @Bean
    public Map<String, Object> aiServiceRegistry(TestGenerator tg,
                                                 FailureAnalyzer fa,
                                                 E2EHeadlessGenerator eg,
                                                 TestEditor te) {
        Map<String, Object> m = new HashMap<>();
        m.put("testGenerator", tg);
        m.put("failureAnalyzer", fa);
        m.put("e2eHeadlessGenerator", eg);
        m.put("testEditor", te);
        return m;
    }
}