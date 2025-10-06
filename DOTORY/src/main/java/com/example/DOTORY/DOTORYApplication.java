package com.example.DOTORY;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class DOTORYApplication {

	public static void main(String[] args) {
		SpringApplication.run(DOTORYApplication.class, args);
	}

}
