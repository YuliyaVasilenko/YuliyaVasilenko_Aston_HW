package com.example.api_gateway.circuitbreaker;

import com.example.common_models.exception.UserNotFoundException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 01.05.2026
 * Description: this class configure Circuit Breakers
 */
@Configuration
public class Resilience4JConfig {

    /**
     * @ Method Name: defaultCustomizer
     * @ Description: Default settings for all services:
     * - Circuit Breaker activates in case 70% of calls result in errors,
     * - Stays in OPEN state for 30 seconds before switching to HALF_OPEN,
     * - Uses the last 10 calls to calculate the error rate (sliding window),
     * - Reduces the amount of information that is generated in the stack trace,
     * - Operations have a timeout of 4 seconds.
     * @ param      : []
     * @ return     : org.springframework.cloud.client.circuitbreaker.Customizer<org.springframework
     * .cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory>
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> {
            CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                    .failureRateThreshold(70)
                    .waitDurationInOpenState(Duration.ofSeconds(30))
                    .slidingWindowSize(10)
                    .writableStackTraceEnabled(false)
                    .build();

            TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                    .timeoutDuration(Duration.ofSeconds(4))
                    .build();

            return new Resilience4JConfigBuilder(id)
                    .circuitBreakerConfig(circuitBreakerConfig)
                    .timeLimiterConfig(timeLimiterConfig)
                    .build();
        });
    }

    /**
     * @ Method Name: userServiceCustomizer
     * @ Description: Custom settings for user-service only (more strict requirements):
     * - Lower failure threshold (50%) for stricter error detection,
     * - Shorter OPEN state duration (20 seconds) for faster recovery attempts,
     * - Larger sliding window (15 calls) for more accurate error rate calculation,
     * - 3-second timeout for operations (stricter than default),
     * - RuntimeException is recorded as an error,
     * - UserNotFoundException is ignored (treated as a business case, not a failure).
     * @ param      : []
     * @ return     : org.springframework.cloud.client.circuitbreaker.Customizer<org.springframework
     * .cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory>
     */
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> userServiceCustomizer() {
        return factory -> {
            CircuitBreakerConfig userServiceConfig = CircuitBreakerConfig.custom()
                    .failureRateThreshold(50)
                    .waitDurationInOpenState(Duration.ofSeconds(20))
                    .slidingWindowSize(15)
                    .recordExceptions(RuntimeException.class)
                    .ignoreExceptions(UserNotFoundException.class)
                    .build();

            TimeLimiterConfig userServiceTimeLimiter = TimeLimiterConfig.custom()
                    .timeoutDuration(Duration.ofSeconds(3))
                    .build();

            factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                    .circuitBreakerConfig(userServiceConfig)
                    .timeLimiterConfig(userServiceTimeLimiter)
                    .build());

            factory.configure(builder -> builder
                    .circuitBreakerConfig(userServiceConfig)
                    .timeLimiterConfig(userServiceTimeLimiter)
                    .build(), "user-service-circuitbreaker");
        };
    }
}
