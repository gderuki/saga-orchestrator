package lv.gderuki.sagapatternsim.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    // this is just a placeholder
    public boolean sendSMS(String orderId) {
        log.info("SMS sent for order: {}", orderId);
        return false;
    }
}
