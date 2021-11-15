package com.example.Driversservice.service;

import com.example.Driversservice.domain.Driver;
import com.example.Driversservice.dto.CreateDriverDTO;
import com.example.Driversservice.dto.OrderMsgDTO;
import com.example.Driversservice.dto.UpdateOrderDTO;
import com.example.Driversservice.exceptions.EntityAlreadyExistsException;
import com.example.Driversservice.exceptions.NoSuchUserException;
import com.example.Driversservice.feign.OrderHistoryServiceClient;
import com.example.Driversservice.feign.OrderServiceClient;
import com.example.Driversservice.repo.DriversRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriversRepo driversRepo;
    private final PasswordEncoder encoder;
    private final OrderServiceClient orderServiceClient;
    private final OrderHistoryServiceClient orderHistoryServiceClient;

    @Transactional
    public Long createDriver(CreateDriverDTO createDriverDTO) {
        if (driversRepo.existsByUsername(createDriverDTO.getUsername())) {
            throw new EntityAlreadyExistsException("User with username " + createDriverDTO.getUsername() +
                    " already exists");
        }
        Driver user = new Driver();
        user.setUsername(createDriverDTO.getUsername());
        user.setPassword(encoder.encode(createDriverDTO.getPassword()));
        user.setFirstName(createDriverDTO.getFirstname());
        user.setLastName(createDriverDTO.getLastname());
        user.setPhoneNumber(createDriverDTO.getPhoneNumber());
        Driver saved = driversRepo.save(user);
        return saved.getId();
    }


    public void updateOrder(UpdateOrderDTO orderForDriverDTO) {
        //check if driver exists
        if (!driversRepo.existsByUsername(orderForDriverDTO.getUsername())) {
            throw new NoSuchUserException("User with username " + orderForDriverDTO.getUsername() +
                    " doesn't exist");
        }
        //delegate to OrdersServiceApp
        orderServiceClient.updateOrder(orderForDriverDTO);
    }

    public List<OrderMsgDTO> getOrderHistory(Long orderId) {
        //just delegate to HistoryService
        return orderHistoryServiceClient.getOrderHistory(orderId);
    }
}
