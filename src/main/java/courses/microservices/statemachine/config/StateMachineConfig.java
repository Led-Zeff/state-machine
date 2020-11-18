package courses.microservices.statemachine.config;

import java.util.EnumSet;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import courses.microservices.statemachine.domain.PaymentEvent;
import courses.microservices.statemachine.domain.PaymentState;
import courses.microservices.statemachine.guard.PaymentIdGuard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {
  
  private final PaymentIdGuard paymentIdGuard;
  private final PreAuthAction preAuthAction;
  private final AuthAction authAction;

  @Override
  public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
    states.withStates()
      .initial(PaymentState.NEW)
      .states(EnumSet.allOf(PaymentState.class))
      .end(PaymentState.AUTH)
      .end(PaymentState.PRE_AUTH_ERROR)
      .end(PaymentState.AUTH_ERROR);
  }

  @Override
  public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
    transitions.withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE).action(preAuthAction)
        .guard(paymentIdGuard)
      .and()
      .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVED)
        .action(context -> log.info("Payment Preaproved"))
      .and()
      .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINED)
        .action(context -> log.info("Payment Predeclined"))
      .and()
      .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.PRE_AUTH).event(PaymentEvent.AUTHORIZE).action(authAction)
      .and()
      .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH).event(PaymentEvent.AUTH_APPROVED)
        .action(context -> log.info("Payment Aproved"))
      .and()
      .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH_ERROR).event(PaymentEvent.AUTH_DECLINED)
        .action(context -> log.info("Payment Declined"));
  }

  @Override
  public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
    StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>() {
      public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
        log.info("State changed from {} to {}", from.getId(), to.getId());
      }
    };

    config.withConfiguration().listener(adapter);
  }

}
