package lv.gderuki.saga.context;

import lombok.Getter;
import lombok.Setter;

@Getter
public class OrderSagaContext {

    private final String idempotencyKey;
    @Setter
    private String orderId;

    public OrderSagaContext(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

}
