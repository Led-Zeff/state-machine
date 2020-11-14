package courses.microservices.statemachine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import courses.microservices.statemachine.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  
}
