package courses.microservices.statemachine;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import courses.microservices.statemachine.domain.PaymentEvent;
import courses.microservices.statemachine.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class StateMachineConfigTest {
  
  @Autowired
  StateMachineFactory<PaymentState, PaymentEvent> factory;

  @Test
  void testStateMachine() {
    StateMachine<PaymentState, PaymentEvent> sm = factory.getStateMachine(UUID.randomUUID());
    sm.start();
    log.info(sm.getState().toString());
    sm.sendEvent(PaymentEvent.PRE_AUTHORIZE);
    log.info(sm.getState().toString());
    sm.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);
    log.info(sm.getState().toString());
    sm.sendEvent(PaymentEvent.PRE_AUTH_DECLINED);
    log.info(sm.getState().toString());
  }

}
