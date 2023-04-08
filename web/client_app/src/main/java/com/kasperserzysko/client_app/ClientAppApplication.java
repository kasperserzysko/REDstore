package com.kasperserzysko.client_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.kasperserzysko.client_app",
        "com.kasperserzysko.security",
        "com.kasperserzysko.data",
        "com.kasperserzysko.web"})
public class ClientAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientAppApplication.class, args);
    }

}
