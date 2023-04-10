package com.kasperserzysko.security;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.kasperserzysko.data",
        "com.kasperserzysko.security",
        "com.kasperserzysko.tools",
        "com.kasperserzysko.email_service"})
public class SecurityApplication {
}
