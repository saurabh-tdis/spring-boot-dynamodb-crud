package com.app.dynamodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class SpringBootDynamodbCrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootDynamodbCrudApplication.class, args);
	}

}
