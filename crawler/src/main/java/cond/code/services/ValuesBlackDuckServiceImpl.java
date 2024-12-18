package cond.code.services;

import cond.code.entities.ValuesBlackDuck;
import cond.code.repositories.ValuesBlackDuckRepository;
import org.springframework.stereotype.Service;

@Service
public class ValuesBlackDuckServiceImpl implements ValuesBlackDuckService {

    private final ValuesBlackDuckRepository valuesBlackDuckRepository;
    public ValuesBlackDuckServiceImpl(ValuesBlackDuckRepository valuesBlackDuckRepository) {
        this.valuesBlackDuckRepository = valuesBlackDuckRepository;
    }

    @Override
    public void createValues(ValuesBlackDuck valuesBlackDuck) {
        valuesBlackDuckRepository.save(valuesBlackDuck);

    }
}
