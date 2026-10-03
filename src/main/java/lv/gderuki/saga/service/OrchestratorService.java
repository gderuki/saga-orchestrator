package lv.gderuki.saga.service;

import lombok.AllArgsConstructor;
import lv.gderuki.saga.exception.SagaExecutionException;
import lv.gderuki.saga.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Service
@AllArgsConstructor
public class OrchestratorService {

    private OrderService orderService;
    private PaymentService paymentService;
    private NotificationService notificationService;

    // this is an old, empirical impl
    // steps:
    // 1. createOrder
    // 2. processPayment
    // 3. sendSMS
    @Deprecated(forRemoval = true)
    public void performTransactionOld(String idempotencyKey) {
        String orderId = orderService.createOrder(idempotencyKey);
        if (orderId == null) {
            throw new RuntimeException("Order creation failed!");
        }

        boolean paymentSuccessful = paymentService.processPayment(orderId);

        if (paymentSuccessful) {
            int retryCount = 0;
            boolean notificationSent = notificationService.sendSMS(orderId);

            while (!notificationSent && retryCount <= 3) {
                if (notificationService.sendSMS(orderId)) {
                    notificationSent = true;
                }
                retryCount++;
            }

            if (!notificationSent) {
                // not sure what kind of exception to throw here
                throw new IllegalStateException("Notification failed, rolling back!");
            }
        } else {
            orderService.rollbackOrder(idempotencyKey);
            throw new RuntimeException("Payment failed, rolling back!");
        }
    }

    /**
     * Executes the given saga steps in order.
     *
     * @param steps the saga steps to execute
     * @param context the context to pass to the saga steps
     */
    @SuppressWarnings("unused")
    public void execute(List<SagaStep<OrderSagaContext>> steps, OrderSagaContext context) {
            Deque<SagaStep<OrderSagaContext>> rollbackStack = new ArrayDeque<>();

        try {
            for (SagaStep<OrderSagaContext> step : steps) {
                boolean success = step.process(context);

                if (success) {
                    rollbackStack.push(step);
                } else {
                    throw new SagaExecutionException("Step execution failed!"); // go to catch
                }
            }
        } catch (Exception e) {
            // rollback transaction
            while (!rollbackStack.isEmpty()) {
                SagaStep<OrderSagaContext> stepToRollback = rollbackStack.pop();
                stepToRollback.rollback(context);
            }

            throw e;
        }
    }

    @SuppressWarnings("unused")
    public void performTransaction(String idempotencyKey) {
        OrderSagaContext context = new OrderSagaContext(idempotencyKey);

        execute(List.of(
            new CreateOrderStep(this.orderService),
            new ProcessPaymentStep(this.paymentService),
            new NotifyCustomerStep(this.notificationService)
        ), context);
    }

}
