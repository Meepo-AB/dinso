package se.meepo.dinso.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.meepo.dinso.database.entity.*;

public interface PensionPlanRepository extends JpaRepository<PensionPlanEntity, String> {
  long countByCompany(CompanyEntity company);

  java.util.List<PensionPlanEntity> findByCompany(CompanyEntity company);
}
