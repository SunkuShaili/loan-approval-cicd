package com.java.spr;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.kafka.annotation.EnableKafka;


@SpringBootApplication
@EnableKafka

public class LoanApprovalCapstoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanApprovalCapstoneApplication.class, args);
    }






}
