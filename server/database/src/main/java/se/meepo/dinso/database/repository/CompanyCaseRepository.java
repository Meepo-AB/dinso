package se.meepo.dinso.database.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.meepo.dinso.database.entity.CompanyCaseEntity;
import se.meepo.dinso.database.entity.CompanyEntity;

public interface CompanyCaseRepository extends JpaRepository<CompanyCaseEntity, String> {
  List<CompanyCaseEntity> findByCompanyOrderByDueOn(CompanyEntity company);
}
