package lv.gderuki.saga.model;

import lv.gderuki.saga.service.PaymentService;

public class ProcessPaymentStep implements SagaStep<OrderSagaContext> {

    private final PaymentService paymentService;

    public ProcessPaymentStep(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public boolean process(OrderSagaContext context) {
        return paymentService.processPayment(context.getOrderId());
    }

    @Override
    public void rollback(OrderSagaContext context) {
        paymentService.refundPayment(context.getOrderId());
    }
}
