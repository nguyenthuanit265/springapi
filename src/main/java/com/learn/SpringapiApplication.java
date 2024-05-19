package com.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
@ComponentScan(basePackages = "com")
public class SpringapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringapiApplication.class, args);
    }

}
