package courses.microservices.statemachine.service;

import org.springframework.statemachine.StateMachine;

import courses.microservices.statemachine.domain.Payment;
import courses.microservices.statemachine.domain.PaymentEvent;
import courses.microservices.statemachine.domain.PaymentState;

public interface PaymentService {
  Payment newPayment(Payment payment);
  StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);
  StateMachine<PaymentState, PaymentEvent> authorize(Long paymentId);
  StateMachine<PaymentState, PaymentEvent> declined(Long paymentId);
}
