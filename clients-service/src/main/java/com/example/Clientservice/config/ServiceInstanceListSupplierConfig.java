package com.example.Clientservice.config;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.ServiceInstanceListSuppliers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ServiceInstanceListSupplierConfig {

    @Bean
    public ServiceInstanceListSupplier supplier() {
        List<ServiceInstance> instances = new ArrayList<>();
        return ServiceInstanceListSuppliers.from("orders-serviceeee", instances.toArray(ServiceInstance[]::new));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
