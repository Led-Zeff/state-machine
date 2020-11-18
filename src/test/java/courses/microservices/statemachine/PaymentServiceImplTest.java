package courses.microservices.statemachine;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import courses.microservices.statemachine.domain.Payment;
import courses.microservices.statemachine.domain.PaymentEvent;
import courses.microservices.statemachine.domain.PaymentState;
import courses.microservices.statemachine.repository.PaymentRepository;
import courses.microservices.statemachine.service.PaymentService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class PaymentServiceImplTest {
  
  @Autowired PaymentService paymentService;
  @Autowired PaymentRepository paymentRepository;

  Payment payment;

  @BeforeEach
  void setUp() {
    payment = Payment.builder().amount(new BigDecimal("12321.43")).build();
  }

  @Test
  @Transactional
  void preAuthTest() {
    Payment saved = paymentService.newPayment(payment);

    paymentService.preAuth(saved.getId());
    Payment preAuth = paymentRepository.getOne(saved.getId());
    log.info(preAuth.toString());
  }

  @Transactional
  @RepeatedTest(10)
  void authTest() {
    Payment saved = paymentService.newPayment(payment);
    StateMachine<PaymentState, PaymentEvent> machine = paymentService.preAuth(saved.getId());

    if (machine.getState().getId() == PaymentState.PRE_AUTH) {
      paymentService.authorize(saved.getId());
    }

    log.info(paymentRepository.getOne(saved.getId()).toString());
  }

}
