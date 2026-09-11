package se.meepo.dinso.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.meepo.dinso.database.entity.*;

public interface EmploymentRepository extends JpaRepository<EmploymentEntity, String> { long countByCompany(CompanyEntity company); java.util.List<EmploymentEntity> findByCompany(CompanyEntity company); }
