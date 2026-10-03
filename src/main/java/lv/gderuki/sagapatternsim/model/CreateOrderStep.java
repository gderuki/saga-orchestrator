package lv.gderuki.sagapatternsim.model;

import lv.gderuki.sagapatternsim.service.OrderService;

public class CreateOrderStep implements SagaStep<OrderSagaContext> {

    private final OrderService orderService;

    public CreateOrderStep(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public boolean process(OrderSagaContext context) {
        String orderId = orderService.createOrder(context.getIdempotencyKey());
        if (orderId != null) {
            context.setOrderId(orderId);
            return true;
        }
        return false;
    }

    @Override
    public void rollback(OrderSagaContext context) {
        orderService.rollbackOrder(context.getIdempotencyKey());
    }
}