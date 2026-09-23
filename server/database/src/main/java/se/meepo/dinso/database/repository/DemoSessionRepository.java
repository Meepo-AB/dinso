package se.meepo.dinso.database.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.meepo.dinso.database.entity.DemoSessionEntity;

public interface DemoSessionRepository extends JpaRepository<DemoSessionEntity, String> {
  Optional<DemoSessionEntity> findByToken(String token);
}
