package lv.gderuki.sagapatternsim.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderService {

    // this is just a placeholder
    public String createOrder(String idempotencyKey) {
        log.info("Creating an order with idempotency key: {}", idempotencyKey);
        return null;
    }

    // this is just a placeholder
    public void rollbackOrder(String idempotencyKey) {
        log.info("Rolling back unsuccessful order: {}", idempotencyKey);
    }

}
