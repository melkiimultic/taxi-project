package com.example.Driversservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DriversServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriversServiceApplication.class, args);
    }

}
