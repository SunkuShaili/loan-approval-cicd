package com.java.spr.kafka.producer;

import com.java.spr.kafka.events.LoanEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoanEventProducer {

    private static final String TOPIC = "loan-events";

    private final KafkaTemplate<String, LoanEvent> kafkaTemplate;

    public LoanEventProducer(KafkaTemplate<String, LoanEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishLoanStatusChange(LoanEvent event) {
        kafkaTemplate.send(TOPIC, event.getLoanId(), event);
    }
}
