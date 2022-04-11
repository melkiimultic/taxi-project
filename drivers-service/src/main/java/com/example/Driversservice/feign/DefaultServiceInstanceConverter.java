package com.example.Driversservice.feign;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationPropertiesBinding
public class DefaultServiceInstanceConverter implements Converter<String, List<DefaultServiceInstance>> {

    //https://www.baeldung.com/configuration-properties-in-spring-boot
    @Override
    public List<DefaultServiceInstance> convert(String source) {
        String[] data = source.split(",");
        if (data.length < 4) {
            throw new IllegalArgumentException();
        }
        List<DefaultServiceInstance> instances = new ArrayList<>();
        instances.add(new DefaultServiceInstance(data[0], data[1], data[2], Integer.parseInt(data[3]), false));
        return instances;
    }
}
