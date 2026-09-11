package se.meepo.dinso.svenskebanken;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import se.meepo.dinso.service.CustomerId;

@SpringBootApplication(scanBasePackages = "se.meepo.dinso")
public class SvenskeBankenApplication { @Bean CustomerId customerId() { return CustomerId.SVENSKEBANKEN; } public static void main(String[] args) { SpringApplication.run(SvenskeBankenApplication.class, args); } }
