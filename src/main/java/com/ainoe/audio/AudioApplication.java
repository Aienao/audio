package com.ainoe.audio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@ComponentScan(basePackages = "com.ainoe")
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@ServletComponentScan
public class AudioApplication {

    public static void main(String[] args) {
        SpringApplication.run(AudioApplication.class, args);
    }

}
