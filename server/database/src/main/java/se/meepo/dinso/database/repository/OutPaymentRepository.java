package se.meepo.dinso.database.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.meepo.dinso.database.entity.OutPaymentEntity;
import se.meepo.dinso.database.entity.PersonEntity;

public interface OutPaymentRepository extends JpaRepository<OutPaymentEntity, String> {
  List<OutPaymentEntity> findByPerson(PersonEntity person);
}
