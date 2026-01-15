package com.java.spr.kafka.consumer;


import com.java.spr.kafka.events.LoanEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LoanEventConsumer {

    @KafkaListener(topics = "loan-events", groupId = "loan-notification-group")
    public void consume(LoanEvent event) {

        if (event.getOldStatus() == null) {
            System.out.println(
                    " Loan CREATED | LoanId=" + event.getLoanId() +
                            " | User=" + event.getActionBy()
            );
        } else {
            System.out.println(
                    " Loan STATUS CHANGED | LoanId=" + event.getLoanId() +
                            " | " + event.getOldStatus() + " → " + event.getNewStatus() +
                            " | By=" + event.getActionBy()
            );
        }
    }



}

