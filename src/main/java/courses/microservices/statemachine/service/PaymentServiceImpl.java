package courses.microservices.statemachine.service;

import javax.transaction.Transactional;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import courses.microservices.statemachine.domain.Payment;
import courses.microservices.statemachine.domain.PaymentEvent;
import courses.microservices.statemachine.domain.PaymentState;
import courses.microservices.statemachine.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  public static final String PAYMENT_ID_HEADER = "payment_id";
  
  private final PaymentRepository paymentRepository;
  private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
  private final PaymenStateChangeLInterceptor paymenStateChangeInterceptor;

  @Override
  public Payment newPayment(Payment payment) {
    payment.setState(PaymentState.NEW);
    return paymentRepository.save(payment);
  }

  @Override
  @Transactional
  public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
    StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
    sendEvent(paymentId, sm, PaymentEvent.PRE_AUTHORIZE);
    return sm;
  }
  
  @Override
  @Transactional
  public StateMachine<PaymentState, PaymentEvent> authorize(Long paymentId) {
    StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
    sendEvent(paymentId, sm, PaymentEvent.AUTHORIZE);
    return sm;
  }
  
  @Override
  @Transactional
  public StateMachine<PaymentState, PaymentEvent> declined(Long paymentId) {
    StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
    sendEvent(paymentId, sm, PaymentEvent.AUTH_DECLINED);
    return sm;
  }

  private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event) {
    Message<PaymentEvent> msg = MessageBuilder.withPayload(event)
      .setHeader(PAYMENT_ID_HEADER, paymentId)
      .build();

    sm.sendEvent(msg);
  }

  private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
    Payment payment = paymentRepository.getOne(paymentId);
    StateMachine<PaymentState, PaymentEvent> sm = stateMachineFactory.getStateMachine(Long.toString(paymentId));

    sm.stop();

    sm.getStateMachineAccessor().doWithAllRegions(sma -> {
      sma.addStateMachineInterceptor(paymenStateChangeInterceptor);
      sma.resetStateMachine(new DefaultStateMachineContext<PaymentState,PaymentEvent>(payment.getState(), null, null, null));
    });

    sm.start();

    return sm;
  }

}
