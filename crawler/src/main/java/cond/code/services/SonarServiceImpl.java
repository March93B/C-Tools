package cond.code.services;

import cond.code.entities.Sonar;
import cond.code.repositories.SonarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class SonarServiceImpl implements SonarService{

    @Autowired
    private SonarRepository sonarRepository;

    @Override
    public void createSonar(Sonar sonar) {
        sonarRepository.save(sonar);
    }

    @Override
    public void deleteSonar(Integer id) {
        Optional<Sonar> sonar = sonarRepository.findById(id);

        if(sonar.isPresent()) {
            sonarRepository.deleteById(id);
        }else {
            throw new IllegalArgumentException("Sonar não encontrado");
        }
    }

    @Override
    public void updateSonar(Sonar sonar) {
        if (sonarRepository.existsById(sonar.getIdSonar())){
            sonarRepository.save(sonar);
        }else{
            throw new RuntimeException("Sonar Não encontrado");
        }
    }

    @Override
    public Sonar getSonarById(Integer id) {
        Optional<Sonar> sonar = sonarRepository.findById(id);
        return sonar.orElseThrow(() -> new RuntimeException("Sonar com ID API " + id + " não encontrado."));

    }

    @Override
    public Sonar getSonarByName(String name) {
        Optional<Sonar> sonar = sonarRepository.findByApiNameSonar(name);
        return sonar.orElseThrow(()-> new RuntimeException("Sonar com Nome API " + name + " não encontrado."));
    }

    @Override
    public Sonar getSonarByUrl(String url) {
        Optional<Sonar> sonar = sonarRepository.findByApiUrlSonar(url);
        return sonar.orElseThrow(()-> new RuntimeException("Sonar com URL "+url+" não encontrado."));
    }
}
