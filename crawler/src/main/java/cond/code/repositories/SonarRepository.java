package cond.code.repositories;

import cond.code.entities.Sonar;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SonarRepository extends JpaRepository<Sonar,Integer> {
    Optional<Sonar> findByNameApi(String Name);
    Optional<Sonar> findByUrlApi(String apiName);
    List<Sonar> findAllByTypeEquals(String type, Sort sort);

    List<Sonar> findAllByActiveProd(boolean activeProd, Sort sort);

    List<Sonar> findAllByTypeEqualsAndActiveProd(String type, boolean activeProd, Sort sort);
}
