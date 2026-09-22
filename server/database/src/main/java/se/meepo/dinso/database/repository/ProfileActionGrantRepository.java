package se.meepo.dinso.database.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.meepo.dinso.database.entity.DemoProfileEntity;
import se.meepo.dinso.database.entity.ProfileActionGrantEntity;

public interface ProfileActionGrantRepository extends JpaRepository<ProfileActionGrantEntity, String> {
  List<ProfileActionGrantEntity> findByProfile(DemoProfileEntity profile);
}
