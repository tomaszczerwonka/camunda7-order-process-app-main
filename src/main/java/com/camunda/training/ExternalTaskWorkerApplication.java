package com.camunda.training;

import org.camunda.bpm.client.spring.annotation.EnableExternalTaskClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableExternalTaskClient(
    baseUrl = "${camunda.bpm.client.base-url}",
    workerId = "${camunda.bpm.client.worker-id}"
)
public class ExternalTaskWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExternalTaskWorkerApplication.class, args);
    }
}