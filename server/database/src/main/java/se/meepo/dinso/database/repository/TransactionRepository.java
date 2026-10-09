package se.meepo.dinso.database.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.meepo.dinso.database.entity.InsuranceEntity;
import se.meepo.dinso.database.entity.TransactionEntity;

public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
  List<TransactionEntity> findByInsuranceIn(List<InsuranceEntity> insurances);
}
