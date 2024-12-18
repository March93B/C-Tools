package cond.code.services;

import cond.code.entities.Sonar;

import java.util.List;

public interface SonarService {
    void createSonar(Sonar sonar);
    void deleteSonar(Integer id);
    void updateSonar(Sonar sonar);
    Sonar getSonarById(Integer id);
    Sonar getSonarByName(String name);
    Sonar getSonarByUrl(String url);
    List<Sonar> getsonars();
    List<Sonar> getBF(String type);
    List<Sonar> getsonarsActiveProd();
    List<Sonar> getsonarsActiveProdBF(String type);
}
