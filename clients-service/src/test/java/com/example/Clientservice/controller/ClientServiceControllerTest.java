package com.example.Clientservice.controller;

import com.example.Clientservice.ResourceConverter;
import com.example.Clientservice.domain.Client;
import com.example.Clientservice.domain.OrderStatus;
import com.example.Clientservice.dto.CreateOrderDTO;
import com.example.Clientservice.dto.OrderMsgDTO;
import com.example.Clientservice.feign.OrderServiceClient;
import com.example.Clientservice.repo.ClientsRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ClientServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClientsRepo clients;
    @Autowired
    private TransactionTemplate template;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private OrderServiceClient orderServiceClient;

    @BeforeEach
    @AfterEach
    public void cleanDB(){
        clients.deleteAll();
    }

    @Test
    @DisplayName("Create user happy path")
    @SneakyThrows
    public void createUser(){
        String body = ResourceConverter.getString(new ClassPathResource("requests/createUser.json"));
        mockMvc.perform(post("/client/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        template.executeWithoutResult(tr->{
            Optional<Client> user = clients.findOneByUsername("test");
            assertTrue(user.isPresent());
            Client client = user.get();
            assertEquals("First",client.getFirstName());
            assertEquals("Last", client.getLastName());
            assertEquals("79031112233",client.getPhoneNumber());
            assertTrue(encoder.matches("test1",client.getPassword()));
        });
    }

    @Test
    @DisplayName("Try to create existing user")
    @SneakyThrows
    public void createExistingUser(){
        Client user = new Client();
        user.setUsername("test");
        user.setPassword(encoder.encode("test1"));
        user.setFirstName("First");
        user.setLastName("Last");
        user.setPhoneNumber("79031112233");
        clients.save(user);
        String body = ResourceConverter.getString(new ClassPathResource("requests/createUser.json"));
        MvcResult mvcResult = mockMvc.perform(post("/client/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("User with username " + user.getUsername() +
                " already exists",contentAsString);
    }

    @Test
    @DisplayName("Create an order happy path")
    @SneakyThrows
    public void createAnOrder(){
        final AtomicReference<Long> userId = new AtomicReference<>();
        template.executeWithoutResult(tr -> {
            Client user = new Client();
            user.setUsername("test");
            user.setPassword(encoder.encode("test1"));
            user.setFirstName("First");
            user.setLastName("Last");
            user.setPhoneNumber("79031112233");
            Client saved = clients.save(user);
            userId.set(saved.getId());
        });

        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setClientId(userId.get());
        String body = mapper.writeValueAsString(dto);

        OrderMsgDTO msg = new OrderMsgDTO();
        msg.setId(1L);
        msg.setStatus(OrderStatus.CREATED);
        msg.setUserId(userId.get());
        LocalDate date = LocalDate.of(2021, 11, 16);
        LocalTime time = LocalTime.of(12, 12);
        msg.setLocalDateTime(LocalDateTime.of(date,time));

        when(orderServiceClient.createOrder(dto)).thenReturn(msg);

        MvcResult mvcResult = mockMvc.perform(post("/client/createOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        OrderMsgDTO response = mapper.readValue(mvcResult.getResponse().getContentAsString(), OrderMsgDTO.class);
        assertTrue(response.equals(msg));
    }

    @Test
    @DisplayName("Create an order if user isn't in DB")
    @SneakyThrows
    public void createOrderByUnsignedUser(){
        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setClientId(-1L);
        String body = mapper.writeValueAsString(dto);
        MvcResult mvcResult = mockMvc.perform(post("/client/createOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("Sign up to create an order",contentAsString);
    }




}