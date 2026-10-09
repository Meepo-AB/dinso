package se.meepo.dinso.springconfig;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import se.meepo.dinso.service.CustomerId;

@Configuration
public class CorsConfiguration {
  private final CustomerId customer;

  public CorsConfiguration(CustomerId customer) {
    this.customer = customer;
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    var configuration = new org.springframework.web.cors.CorsConfiguration();
    configuration.setAllowedOrigins(List.of(localClientOrigin()));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    configuration.setMaxAge(3600L);

    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);
    return source;
  }

  private String localClientOrigin() {
    return switch (customer) {
      case SVENSKEBANKEN -> "http://localhost:5173";
      case PENSIONSBOLAGET -> "http://localhost:5174";
      case FINBANKEN -> "http://localhost:5175";
    };
  }
}
