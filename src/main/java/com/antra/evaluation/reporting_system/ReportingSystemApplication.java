package com.antra.evaluation.reporting_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value="com.antra.evaluation.reporting_system")
public class ReportingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportingSystemApplication.class, args);
    }

}
