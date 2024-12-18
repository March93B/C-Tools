package cond.code.repositories;

import cond.code.entities.ValuesSonar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValuesSonarRepository extends JpaRepository<ValuesSonar, Integer> {


}
