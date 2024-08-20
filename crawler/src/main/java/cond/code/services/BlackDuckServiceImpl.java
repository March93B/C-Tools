package cond.code.services;

import cond.code.entities.BlackDuck;
import cond.code.repositories.BlackDuckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class BlackDuckServiceImpl implements BlackDuckService {

    @Autowired
    private BlackDuckRepository blackDuckRepository;

    @Override
    public void createBlackDuck(BlackDuck blackDuck) {
        if (blackDuckRepository.existsByNameApiBlackDuck(blackDuck.getNameApiBlackDuck())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nome da Api já existente (verifique se colocou )");
        }

        blackDuckRepository.save(blackDuck);
    }
    @Override
    public BlackDuck getBlackDuckId(Integer id) {
        Optional<BlackDuck> blackDuck = blackDuckRepository.findById(id);
        return blackDuck.orElseThrow(() -> new RuntimeException("BlackDuck com ID " + id + " não encontrado."));
    }

    @Override
    public BlackDuck getBlackDuckByApiName(String apiName) {
        Optional<BlackDuck> blackDuck = blackDuckRepository.findByUrlApiBlackDuck(apiName);
        return blackDuck.orElseThrow(() -> new RuntimeException("BlackDuck com URL API " + apiName + " não encontrado."));
    }

    @Override
    public void updateBlackDuck(BlackDuck blackDuck) {
        if (blackDuckRepository.existsById(blackDuck.getIdBlackDuck())) {
            blackDuckRepository.save(blackDuck);
        } else {
            throw new RuntimeException("BlackDuck com ID " + blackDuck.getIdBlackDuck() + " não encontrado.");
        }
    }
    @Override
    public void deleteBlackDuck(Integer id) {
        Optional<BlackDuck> blackDuck = blackDuckRepository.findById(id);

        if (blackDuck.isPresent()) {
            blackDuckRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("BlackDuck não encontrado");
        }
    }

    @Override
    public BlackDuck getBlackDuckByUrl(String url) {
        Optional<BlackDuck> blackDuck = blackDuckRepository.findByUrlApiBlackDuck(url);
        return blackDuck.orElseThrow(()-> new RuntimeException("BlackDuck com URL " + url + " não encontrado."));
    }

    @Override
    public List<BlackDuck> getBlackDucks() {
        List<BlackDuck> blackDucks = blackDuckRepository.findAll();
        return blackDucks;
    }
}
