package cond.code.repositories;

import cond.code.entities.Sonar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SonarRepository extends JpaRepository<Sonar,Integer> {
    Optional<Sonar> findByApiNameSonar(String Name);
    Optional<Sonar> findByApiUrlSonar(String apiName);
}
