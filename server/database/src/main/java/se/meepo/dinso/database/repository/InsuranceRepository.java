package se.meepo.dinso.database.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.meepo.dinso.database.entity.*;

public interface InsuranceRepository extends JpaRepository<InsuranceEntity, String> {
  List<InsuranceEntity> findByPerson(PersonEntity person);
}
