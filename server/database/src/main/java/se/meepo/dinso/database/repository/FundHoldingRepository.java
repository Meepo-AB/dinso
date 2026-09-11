package se.meepo.dinso.database.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.meepo.dinso.database.entity.FundHoldingEntity;
import se.meepo.dinso.database.entity.InsuranceEntity;

public interface FundHoldingRepository extends JpaRepository<FundHoldingEntity, String> { List<FundHoldingEntity> findByInsurance(InsuranceEntity insurance); void deleteByInsurance(InsuranceEntity insurance); }
