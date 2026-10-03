package lv.gderuki.saga.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentService {

    // this is just a placeholder
    public boolean processPayment(String orderId) {
        log.info("Processing payment for orderId: {}", orderId);
        return false;
    }

    // this is just a placeholder
    public void refundPayment(String orderId) {
        log.info("Refunding payment: {}", orderId);
    }

}
