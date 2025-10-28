package com.example.demo.Runner.listener;


import com.example.demo.Runner.Runner;
import com.example.demo.orchestrator.mq.RunRequested;
import com.example.demo.orchestrator.mq.Topics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RunRequestListener {

    @KafkaListener(topics = Topics.RUN_REQUESTED_V1, groupId = "runner-pool", concurrency = "3")
    public void handle(RunRequested msg) {
       //Runner.run();
        System.out.println("Received RunRequested: " );
    }
}
