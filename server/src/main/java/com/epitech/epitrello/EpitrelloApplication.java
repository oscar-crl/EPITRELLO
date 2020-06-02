package com.epitech.epitrello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class EpitrelloApplication {

	public static void main(String[] args) {
		SpringApplication.run(EpitrelloApplication.class, args);
	}

}
