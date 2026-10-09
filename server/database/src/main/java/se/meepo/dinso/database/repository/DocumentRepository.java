package se.meepo.dinso.database.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.meepo.dinso.database.entity.DocumentEntity;
import se.meepo.dinso.database.entity.PersonEntity;

public interface DocumentRepository extends JpaRepository<DocumentEntity, String> {
  List<DocumentEntity> findByPerson(PersonEntity person);
}
