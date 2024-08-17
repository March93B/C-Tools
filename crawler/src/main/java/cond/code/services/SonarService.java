package cond.code.services;

import cond.code.entities.Sonar;

public interface SonarService {
    void createSonar(Sonar sonar);
    void deleteSonar(Integer id);
    void updateSonar(Sonar sonar);
    Sonar getSonarById(Integer id);
    Sonar getSonarByName(String name);
    Sonar getSonarByUrl(String url);
}
