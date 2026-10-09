package se.meepo.dinso.springconfig;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.meepo.dinso.service.CustomerRules;

@Configuration
@EnableConfigurationProperties(CustomerRuleProperties.class)
public class CustomerRulesConfiguration {
  @Bean
  CustomerRules customerRules(CustomerRuleProperties properties) {
    return properties.toRules();
  }
}
