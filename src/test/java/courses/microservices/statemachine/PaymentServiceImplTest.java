package courses.microservices.statemachine;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import courses.microservices.statemachine.domain.Payment;
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
  void preAuth() {
    Payment saved = paymentService.newPayment(payment);

    paymentService.preAuth(saved.getId());
    Payment preAuth = paymentRepository.getOne(saved.getId());
    log.info(preAuth.toString());
  }

}
