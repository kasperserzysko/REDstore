package com.kasperserzysko.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.kasperserzysko.cache",
        "com.kasperserzysko.web",
        "com.kasperserzysko.tools",
        "com.kasperserzysko.data",
        "com.kasperserzysko.cache"
        })
public class WebApplication {


}
