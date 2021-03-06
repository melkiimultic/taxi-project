package com.example.Clientservice.service;

import com.example.Clientservice.domain.Client;
import com.example.Clientservice.dto.CreateClientDTO;
import com.example.Clientservice.dto.CreateOrderDTO;
import com.example.Clientservice.dto.OrderMsgDTO;
import com.example.Clientservice.exceptions.EntityAlreadyExistsException;
import com.example.Clientservice.feign.OrderServiceClient;
import com.example.Clientservice.repo.ClientsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientsRepo clientsRepo;
    private final PasswordEncoder encoder;
    private final OrderServiceClient orderServiceClient;


    @Transactional
    public Long createClient(CreateClientDTO createClientDTO) {
        if (clientsRepo.existsByUsername(createClientDTO.getUsername())) {
            throw new EntityAlreadyExistsException("User with username " + createClientDTO.getUsername() +
                    " already exists");
        }
        Client user = new Client();
        user.setUsername(createClientDTO.getUsername());
        user.setPassword(encoder.encode(createClientDTO.getPassword()));
        user.setFirstName(createClientDTO.getFirstname());
        user.setLastName(createClientDTO.getLastname());
        user.setPhoneNumber(createClientDTO.getPhoneNumber());
        Client saved = clientsRepo.save(user);
        return saved.getId();
    }

    public OrderMsgDTO createOrder(CreateOrderDTO createOrderDTO) {
        if (clientsRepo.existsById(createOrderDTO.getClientId())) {
            return orderServiceClient.createOrder(createOrderDTO);
        }
        throw new EntityNotFoundException("Sign up to create an order");
    }

}
