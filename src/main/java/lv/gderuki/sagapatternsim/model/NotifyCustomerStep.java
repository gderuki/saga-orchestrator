package lv.gderuki.sagapatternsim.model;

import lv.gderuki.sagapatternsim.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotifyCustomerStep implements SagaStep<OrderSagaContext> {

    private static final Logger log = LoggerFactory.getLogger(NotifyCustomerStep.class);

    private final NotificationService notificationService;

    public NotifyCustomerStep(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public boolean process(OrderSagaContext context) {
        int retries = 3;

        for (int i = 0; i < retries; i++) {
            boolean success = notificationService.sendSMS(context.getOrderId());

            if (success) {
                return true;
            }
        }

        // TODO: add some worker with queue here, to retry sending SMS after 24h or something
        log.error("Failed to notify customer after {} retries", retries);

        return true; // true here because we don't want to rollback
    }

    @Override
    public void rollback(OrderSagaContext context) {
        // this is a pivotal step, no need to rollback
    }
}
