package cond.code.services;

import cond.code.entities.BlackDuck;

import java.util.List;

public interface BlackDuckService {
    void createBlackDuck(BlackDuck blackDuck);
    void updateBlackDuck(BlackDuck blackDuck);
    void deleteBlackDuck(Integer id);
    BlackDuck getBlackDuckId(Integer id);
    BlackDuck getBlackDuckByApiName(String apiName);
    BlackDuck getBlackDuckByUrl(String url);
    List<BlackDuck> getBlackDucks();
    List<BlackDuck> getBlackDucksActiveProd();
    void foundyey(List<BlackDuck> blackDucks, String cookie, String cookie2, String envv, int a, int b) throws Exception;
    int getProgress();
}