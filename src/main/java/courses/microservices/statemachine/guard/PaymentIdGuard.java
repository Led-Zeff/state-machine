package courses.microservices.statemachine.guard;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

import courses.microservices.statemachine.domain.PaymentEvent;
import courses.microservices.statemachine.domain.PaymentState;
import courses.microservices.statemachine.service.PaymentServiceImpl;

@Component
public class PaymentIdGuard implements Guard<PaymentState, PaymentEvent> {

  @Override
  public boolean evaluate(StateContext<PaymentState, PaymentEvent> context) {
    return context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) != null;
  }
  
}
