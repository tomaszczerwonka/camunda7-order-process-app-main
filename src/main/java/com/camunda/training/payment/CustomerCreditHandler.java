package com.camunda.training.payment;

import com.camunda.training.services.CustomerService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription(value = "creditDeduction", lockDuration = 10000)
@RequiredArgsConstructor
public class CustomerCreditHandler implements ExternalTaskHandler {

    private final CustomerService customerService;
    private static final Logger LOG = LoggerFactory.getLogger(CustomerCreditHandler.class);

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        String customerId = externalTask.getVariable("customerId");
        Double orderTotal = externalTask.getVariable("orderTotal");
        LOG.info("handle topic {} for task id {} and customer id {}", externalTask.getTopicName(), externalTask.getId(), customerId);
        Double openAmount = customerService.deductCredit(customerId, orderTotal);
        Double customerCredit = customerService.getCustomerCredit(customerId);
        Map<String, Object> variables = new HashMap<>();
        variables.put("openAmount", openAmount);
        variables.put("customerCredit", customerCredit);

        externalTaskService.complete(externalTask, variables);
    }
}
