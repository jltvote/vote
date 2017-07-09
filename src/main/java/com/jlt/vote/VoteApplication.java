package com.jlt.vote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages={"com.jlt.vote","com.xcrm.cloud.database"})
public class VoteApplication {
	public static void main(String[] args) {
		SpringApplication.run(VoteApplication.class, args);
	}
}
