package march.services;

import march.entities.GitHub;

import java.io.IOException;
import java.util.List;

public interface CodeQLService {
    int getProgress();

    void getLatestReleaseBranch(String githubToken, List<GitHub> gitHubs);

    void getLatestReleaseBranchProdUAT(List<GitHub> gitHubs, int b);

    void getVulnerabilities(List<GitHub> gitHubs, String cookieValue, int b) throws IOException;

}
