package com.example.demo.orchestrator.mq;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


    @Component
    public class RunRequestPublisher {
        private final KafkaTemplate<String, Object> kafka;
        public RunRequestPublisher(KafkaTemplate<String, Object> kafka) { this.kafka = kafka; }

        public void publish(RunRequested msg) {
            // use runId as key for stable partitioning
            kafka.send(Topics.RUN_REQUESTED_V1, msg.runId(), msg);
        }
    }
