package com.DADN.homeyolo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HomeyoloApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeyoloApplication.class, args);
	}

}
