package cond.code.services;

import cond.code.entities.ValuesSonar;
import cond.code.repositories.ValuesSonarRepository;
import org.springframework.stereotype.Service;

@Service
public class ValuesSonarServiceImpl implements ValuesSonarService {
    private final ValuesSonarRepository valuesSonarRepository;
    public ValuesSonarServiceImpl(ValuesSonarRepository valuesSonarRepository) {
        this.valuesSonarRepository = valuesSonarRepository;
    }
    @Override
    public void createValue(ValuesSonar valuesSonar) {
        valuesSonarRepository.save(valuesSonar);
    }
}
