package cond.code.repositories;

import cond.code.entities.BlackDuck;
import cond.code.entities.Seeker;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeekerRepository extends JpaRepository<Seeker,Integer> {
    Optional<Seeker> findByUrlApi(String Url);
    Optional<Seeker> findAllByNameApi(String apiNameSeeker);
    boolean existsByNameApi(String name);

    List<Seeker> findAllByActiveProd(boolean activeProd, Sort sort);
}
