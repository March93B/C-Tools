package cond.code.repositories;

import cond.code.entities.BlackDuck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BlackDuckRepository extends JpaRepository<BlackDuck, Integer> {

    Optional<BlackDuck> findByUrlApiBlackDuck(String Url);
    Optional<BlackDuck> findAllByNameApiBlackDuck(String apiNameBlackDuck);
    boolean existsByNameApiBlackDuck(String name);

}
