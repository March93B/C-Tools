package cond.code.services;

import cond.code.entities.Ambiente;
import cond.code.repositories.AmbienteRepository;
import org.springframework.stereotype.Service;

@Service
public class AmbienteServiceImpl implements AmbienteService {

    private AmbienteRepository ambienteRepository;

    public AmbienteServiceImpl(AmbienteRepository ambienteRepository) {
        this.ambienteRepository = ambienteRepository;
    }

    @Override
    public void create(Ambiente ambiente) {
        if (ambienteRepository.existsAmbienteByNomeAmbiente(ambiente.getNomeAmbiente())){
            throw new RuntimeException("Ambiente já existente");
        }
        ambienteRepository.save(ambiente);
    }

    @Override
    public void update(Ambiente ambiente) {

    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public Ambiente getAmbienteByName(String name) {
        return null;
    }
}
