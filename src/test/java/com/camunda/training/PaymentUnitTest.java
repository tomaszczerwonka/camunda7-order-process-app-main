package com.camunda.training;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.externalTask;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.findId;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.withVariables;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.community.process_test_coverage.junit5.platform7.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ProcessEngineCoverageExtension.class)
@Deployment(resources = {"Payment process.bpmn"})
public class PaymentUnitTest {

    @Test
    public void happy_path_test() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("PaymentProcess",
            withVariables("orderTotal", 45.99, "customerCredit", 30.00));
        assertThat(processInstance).isWaitingAt(findId("Deduct customer credit")).externalTask()
            .hasTopicName("creditDeduction");
        complete(externalTask());
        assertThat(processInstance).isWaitingAt(findId("Charge credit card")).externalTask()
            .hasTopicName("creditCardCharging");
        complete(externalTask());
        assertThat(processInstance).isEnded().hasPassed(findId("Payment completed"));
    }

}
