package se.meepo.dinso.pensionsbolaget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import se.meepo.dinso.service.CustomerId;

@SpringBootApplication(scanBasePackages = "se.meepo.dinso")
public class PensionsBolagetApplication {
  @Bean
  CustomerId customerId() {
    return CustomerId.PENSIONSBOLAGET;
  }

  public static void main(String[] args) {
    SpringApplication.run(PensionsBolagetApplication.class, args);
  }
}
