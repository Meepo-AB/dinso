package se.meepo.dinso.finbanken;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import se.meepo.dinso.service.CustomerId;

@SpringBootApplication(scanBasePackages = "se.meepo.dinso")
public class FinBankenApplication {
  @Bean
  CustomerId customerId() {
    return CustomerId.FINBANKEN;
  }

  public static void main(String[] args) {
    SpringApplication.run(FinBankenApplication.class, args);
  }
}
