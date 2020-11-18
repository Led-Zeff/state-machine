package courses.microservices.statemachine.config;

import java.util.Random;

import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import courses.microservices.statemachine.domain.PaymentEvent;
import courses.microservices.statemachine.domain.PaymentState;
import courses.microservices.statemachine.service.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PreAuthAction implements Action<PaymentState, PaymentEvent> {

  @Override
  public void execute(StateContext<PaymentState, PaymentEvent> context) {
    log.info("Pre auth was called");

    PaymentEvent paymentEvent;
    if (new Random().nextInt(10) < 8) {
      log.info("Pre Approved");
      paymentEvent = PaymentEvent.PRE_AUTH_APPROVED;
    } else {
      log.info("Pre Declined");
      paymentEvent = PaymentEvent.PRE_AUTH_DECLINED;
    }

    context.getStateMachine().sendEvent(MessageBuilder
      .withPayload(paymentEvent)
      .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
      .build()
    );
  }
  
}
