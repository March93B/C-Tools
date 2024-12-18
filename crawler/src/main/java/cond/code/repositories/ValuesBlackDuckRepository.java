package cond.code.repositories;

import cond.code.entities.ValuesBlackDuck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValuesBlackDuckRepository extends JpaRepository<ValuesBlackDuck, Integer> {
}
