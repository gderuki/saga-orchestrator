package lv.gderuki.saga.step;

import lv.gderuki.saga.circuitbreaker.CircuitBreaker;
import lv.gderuki.saga.context.OrderSagaContext;
import lv.gderuki.saga.client.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotifyCustomerStep implements SagaStep<OrderSagaContext> {

    private static final Logger log = LoggerFactory.getLogger(NotifyCustomerStep.class);

    private final NotificationService notificationService;
    private final CircuitBreaker circuitBreaker;


    public NotifyCustomerStep(NotificationService notificationService, CircuitBreaker circuitBreaker) {
        this.notificationService = notificationService;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public boolean process(OrderSagaContext context) {
        int attempts = 0;
        while (attempts < 3) {
            if (!circuitBreaker.allowRequest()) {
                log.error("Circuit breaker is open! Call blocked.");
                return true; // Pivot step — не роняем сагу
            }

            try {
                boolean sent = notificationService.sendSMS(context.getOrderId());
                if (sent) {
                    circuitBreaker.recordSuccess();
                    return true;
                } else {
                    circuitBreaker.recordFailure();
                }
            } catch (Exception e) {
                circuitBreaker.recordFailure();
            }
            attempts++;
        }
        return true; // Проглатываем ошибку для Pivot Step
    }

    @Override
    public void rollback(OrderSagaContext context) {
        // this is a pivotal step, no need to rollback
    }
}
