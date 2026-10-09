package se.meepo.dinso.database;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "se.meepo.dinso.database.entity")
@EnableJpaRepositories(basePackages = "se.meepo.dinso.database.repository")
public class DatabaseConfiguration {}
