package com.camunda.training.payment;

import com.camunda.training.services.CreditCardService;
import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription("creditCardCharging")
@RequiredArgsConstructor
public class CreditCardHandler implements ExternalTaskHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CreditCardHandler.class);

    private final CreditCardService creditCardService;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        try {
            LOG.info("handle topic {} for task id {}", externalTask.getTopicName(), externalTask.getId());
            String cardNumber = externalTask.getVariable("cardNumber");
            String cvc = externalTask.getVariable("CVC");
            String expiryDate = externalTask.getVariable("expiryDate");
            Double openAmount = externalTask.getVariable("openAmount");
            openAmount = openAmount == null ? 0 : openAmount;
            creditCardService.chargeAmount(cardNumber, cvc, expiryDate, openAmount);

            externalTaskService.complete(externalTask);
        } catch (IllegalArgumentException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            externalTaskService.handleBpmnError(externalTask, "creditCardChargeError");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            externalTaskService.handleFailure(externalTask, "unexpected error happened", sw.toString(), 0, 0);
        }
    }
}
