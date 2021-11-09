package com.example.Clientservice.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class PropertyParsingConfig {

    //https://www.baeldung.com/configuration-properties-in-spring-boot
    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, List<DefaultServiceInstance>> propertiesConverter() {
        return new Converter<>() {
            @SneakyThrows
            @Override
            public List<DefaultServiceInstance> convert(String source) {
                JsonNode jsonNode = new ObjectMapper().readTree(source);
                if (!jsonNode.isArray()) {
                    throw new IllegalArgumentException();
                }
                ArrayNode arrayNode = (ArrayNode) jsonNode;
                List<DefaultServiceInstance> instances = new ArrayList<>();
                arrayNode.forEach(it -> {
                    instances.add(new DefaultServiceInstance(
                                    it.get("instanceId").asText(),
                                    it.get("serviceId").asText(),//not needed
                                    it.get("host").asText(),
                                    it.get("port").asInt(),
                                    true

                            )
                    );
                });
                return instances;
            }
        };
    }

}
