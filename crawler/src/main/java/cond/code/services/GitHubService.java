package cond.code.services;

import cond.code.entities.GitHub;

import java.util.List;

public interface GitHubService {
    void createGit(GitHub gitHub);
    void updateGit(GitHub gitHub);
    void deleteGit(Integer id);

    List<GitHub> getGitHubs();
    List<GitHub> getGitHubsActiveProd();

}
