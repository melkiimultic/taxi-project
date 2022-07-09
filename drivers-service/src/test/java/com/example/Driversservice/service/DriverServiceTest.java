package com.example.Driversservice.service;

import com.example.Driversservice.domain.OrderStatus;
import com.example.Driversservice.dto.UpdateOrderDTO;
import com.example.Driversservice.feign.OrderHistoryServiceClient;
import com.example.Driversservice.feign.OrderServiceClient;
import com.example.Driversservice.repo.DriversRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {
    @InjectMocks
    private DriverService driverService;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private DriversRepo driversRepo;
    @Mock
    private OrderServiceClient orderServiceClient;
    @Mock
    private OrderHistoryServiceClient orderHistoryServiceClient;


    @Test
    @DisplayName("Update with driver that doesn't exist")
    public void updateWithWrongDriver() {
        UpdateOrderDTO uo = new UpdateOrderDTO();
        uo.setDriver("Driver");
        uo.setOrderId(1L);
        uo.setStatus(OrderStatus.CREATED);
        final EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> driverService.updateOrder(uo));
        assertEquals("Driver with username " + uo.getDriver() +
                " doesn't exist", entityNotFoundException.getMessage());
        verify(driversRepo, times(1)).existsByUsername("Driver");
        verify(orderServiceClient, never()).updateOrder(uo);

    }

    @Test
    @DisplayName("Successful update")
    public void happyPathUpdate() {
        UpdateOrderDTO uo = new UpdateOrderDTO();
        uo.setDriver("Driver");
        uo.setOrderId(1L);
        uo.setStatus(OrderStatus.CREATED);
        when(driversRepo.existsByUsername(anyString())).thenReturn(true);
        driverService.updateOrder(uo);
        verify(orderServiceClient, times(1)).updateOrder(uo);
    }

}


