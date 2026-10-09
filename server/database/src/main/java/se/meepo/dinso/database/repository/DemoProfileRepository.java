package se.meepo.dinso.database.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import se.meepo.dinso.database.entity.DemoProfileEntity;
import se.meepo.dinso.service.CustomerId;

public interface DemoProfileRepository extends JpaRepository<DemoProfileEntity, String> {
  List<DemoProfileEntity> findByCustomerId(CustomerId customerId);

  Optional<DemoProfileEntity> findByCustomerIdAndExternalId(
      CustomerId customerId, String externalId);
}
