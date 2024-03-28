package com.camunda.training.payment;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription(value = "creditRestore")
@RequiredArgsConstructor
class RestoreCustomerCreditHandler implements ExternalTaskHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestoreCustomerCreditHandler.class);

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        LOG.info("handle topic {} for task id {}", externalTask.getTopicName(), externalTask.getId());
        externalTaskService.complete(externalTask);
    }
}
