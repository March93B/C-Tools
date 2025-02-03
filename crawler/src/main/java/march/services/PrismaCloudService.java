package march.services;

import march.entities.GitHub;

import java.io.IOException;
import java.util.List;

public interface PrismaCloudService {
    void getLatestReleaseBranch(String githubToken, List<GitHub> gitHubs);

    void getLatestReleaseBranchProdUAT(List<GitHub> gitHubs, int b);

    void getVulnerabilities(String githubToken, List<GitHub> gitHubs, String cookieValue, int b) throws IOException;

    int getProgress();
}
