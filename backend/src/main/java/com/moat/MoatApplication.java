package com.moat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MoatApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoatApplication.class, args);
    }
}
