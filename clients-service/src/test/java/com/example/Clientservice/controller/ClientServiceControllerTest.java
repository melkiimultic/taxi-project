package com.example.Clientservice.controller;

import com.example.Clientservice.ResourceConverter;
import com.example.Clientservice.domain.Client;
import com.example.Clientservice.repo.ClientsRepo;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ClientServiceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ClientsRepo clients;

    @Autowired
    TransactionTemplate template;

    @Autowired
    PasswordEncoder encoder;


    @Test
    @DisplayName("Create user happy path")
    @SneakyThrows
    public void createUser(){
        clients.deleteAll();
        String body = ResourceConverter.getString(new ClassPathResource("requests/createUser.json"));
        mockMvc.perform(post("/client/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        template.executeWithoutResult(tr->{
            Optional<Client> user = clients.findOneByUsername("test");
            assertEquals(true,user.isPresent());
            Client client = user.get();
            assertEquals("First",client.getFirstName());
            assertEquals("Last", client.getLastName());
            assertEquals("79031112233",client.getPhoneNumber());
            assertTrue(encoder.matches("test1",client.getPassword()));
        });
    }

}