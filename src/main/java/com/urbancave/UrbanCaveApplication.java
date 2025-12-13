package com.urbancave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UrbanCaveApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrbanCaveApplication.class, args);
    }

}
