package com.example.DOTORY;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DOTORYApplication {

	public static void main(String[] args) {
		//--- 디버깅용 코드 시작 ---
		System.out.println("--- 실제 적용된 DB 정보 ---");
		System.out.println("DB_HOST: " + System.getenv("DB_HOST"));
		System.out.println("DB_USERNAME: " + System.getenv("DB_USERNAME"));
		System.out.println("DB_PASSWORD: " + System.getenv("DB_PASSWORD"));
		System.out.println("--------------------------");
		//--- 디버깅용 코드 끝 ---

		SpringApplication.run(DOTORYApplication.class, args);
	}

}
