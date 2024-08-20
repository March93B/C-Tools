package cond.code.repositories;

import cond.code.entities.Ambiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmbienteRepository extends JpaRepository<Ambiente, Integer> {

    Ambiente findByNomeAmbiente(String nome);
    boolean existsAmbienteByNomeAmbiente(String name);

}
