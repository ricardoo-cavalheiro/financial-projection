package com.example.financial_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@CommandScan
@SpringBootApplication
public class FinancialAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(FinancialAppApplication.class, args);
	}
}
