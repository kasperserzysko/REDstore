package com.kasperserzysko.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.kasperserzysko.cache",
        "com.kasperserzysko.web"
        })
@EnableCaching
public class WebApplication {


}
