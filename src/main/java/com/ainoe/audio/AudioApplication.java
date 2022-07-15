package com.ainoe.audio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableAspectJAutoProxy(exposeProxy = true)
@ComponentScan(basePackages = "com.ainoe")
@SpringBootApplication
public class AudioApplication {

    public static void main(String[] args) {
        SpringApplication.run(AudioApplication.class, args);
    }

}
