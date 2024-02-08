package com.jsp.sap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MeeshoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeeshoApplication.class, args);
	}

}
