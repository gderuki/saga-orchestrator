package lv.gderuki.saga.service;

import lv.gderuki.saga.exception.SagaExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrchestratorServiceTest {

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrchestratorService orchestratorService;

    @Test
    public void shouldExecuteSuccessfullyWhenAllStepsSucceed() {
        // Given
        final String idempotencyKey = "KEY-123";
        final String orderId = "ORDER-123";

        when(orderService.createOrder(idempotencyKey)).thenReturn(orderId);
        when(paymentService.processPayment(orderId)).thenReturn(true);
        when(notificationService.sendSMS(orderId)).thenReturn(true);

        // When
        orchestratorService.performTransaction(idempotencyKey);

        // Then
        verify(orderService, times(1)).createOrder(idempotencyKey);
        verify(paymentService, times(1)).processPayment(orderId);
        verify(notificationService, times(1)).sendSMS(orderId);

        verify(orderService, never()).rollbackOrder(orderId);
    }

    @Test
    public void shouldRollbackOrderOnPaymentFailure() {
        // Given
        final String idempotencyKey = "ABC-123";
        final String orderId = "ORDER-123";

        when(orderService.createOrder(idempotencyKey)).thenReturn(orderId);
        when(paymentService.processPayment(orderId)).thenReturn(false);

        // When
        assertThrows(SagaExecutionException.class, () -> orchestratorService.performTransaction(idempotencyKey));

        // Then
        verify(orderService, times(1)).createOrder(idempotencyKey);
        verify(paymentService, times(1)).processPayment(orderId);
        verify(orderService, times(1)).rollbackOrder(idempotencyKey);
    }

    @Test
    public void shouldNotRollbackWhenNotificationFails() {
        // Given
        final String idempotencyKey = "KEY-123";
        final String orderId = "ORDER-123";

        when(orderService.createOrder(idempotencyKey)).thenReturn(orderId);
        when(paymentService.processPayment(orderId)).thenReturn(true);
        when(notificationService.sendSMS(orderId)).thenReturn(false, false, false);

        // When
        orchestratorService.performTransaction(idempotencyKey);

        // Then
        verify(orderService, times(1)).createOrder(idempotencyKey);
        verify(paymentService, times(1)).processPayment(orderId);
        verify(notificationService, times(3)).sendSMS(orderId);

        verify(orderService, never()).rollbackOrder(idempotencyKey);
    }

    @Test
    public void shouldHaveNoInteractionsWhenFailsOnFirstStep() {
        // Given
        final String idempotencyKey = "KEY-123";
        when(orderService.createOrder(idempotencyKey)).thenReturn(null);

        // When
        assertThrows(SagaExecutionException.class, () -> orchestratorService.performTransaction(idempotencyKey));

        // Then
        verify(orderService, times(1)).createOrder(idempotencyKey);
        verify(orderService, never()).rollbackOrder(anyString());

        verifyNoInteractions(paymentService);
        verifyNoInteractions(notificationService);
    }

}
