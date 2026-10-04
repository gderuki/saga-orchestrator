package lv.gderuki.saga.config;

import lv.gderuki.saga.circuitbreaker.CircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfig {

    @Bean
    public CircuitBreaker circuitBreaker() {
        return new CircuitBreaker(3, 5000); // 3 failures, 5 seconds timeout
    }

}
