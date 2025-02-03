package cond.code.services;

import cond.code.entities.GitHub;
import cond.code.repositories.GitHubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class GitHubServiceImpl implements GitHubService {

    @Autowired
    private GitHubRepository githubRepository;

    @Override
    public void createGit(GitHub gitHub) {
        if(githubRepository.existsByNameApi(gitHub.getNameApi())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"nome já existente");
        }
        githubRepository.save(gitHub);
    }

    @Override
    public void updateGit(GitHub gitHub) {
        if (githubRepository.existsById(gitHub.getIdGit())) {
            GitHub existingGit = githubRepository.findById(gitHub.getIdGit())
                    .orElseThrow(() -> new RuntimeException("Sonar Não encontrado"));

            if (gitHub.getNameApi() != null && !gitHub.getNameApi().isEmpty()) {
                existingGit.setNameApi(gitHub.getNameApi());
            } else if (existingGit.getNameApi() == null) {
                existingGit.setNameApi(existingGit.getNameApi());
            }

            if (gitHub.getUrlApi() != null && !gitHub.getUrlApi().isEmpty()) {
                existingGit.setUrlApi(gitHub.getUrlApi());
            } else if (existingGit.getUrlApi() == null) {
                existingGit.setUrlApi(existingGit.getUrlApi());
            }

            if (gitHub.getType() != null && !gitHub.getType().isEmpty()) {
                existingGit.setType(gitHub.getType());
            } else if (existingGit.getType() == null) {
                existingGit.setType(existingGit.getType());
            }
            if (gitHub.isActiveProd() != null) {
                existingGit.setActiveProd(gitHub.isActiveProd());
            }
            if (gitHub.getReleasesPROD() != null && !gitHub.getReleasesPROD().isEmpty()) {
                existingGit.setReleasesPROD(gitHub.getReleasesPROD());
            } else if (existingGit.getReleasesPROD() == null) {
                existingGit.setReleasesPROD(existingGit.getReleasesPROD());
            }
            if (gitHub.getReleasesUAT() != null && !gitHub.getReleasesUAT().isEmpty()) {
                existingGit.setReleasesUAT(gitHub.getReleasesUAT());
            } else if (existingGit.getReleasesUAT() == null) {
                existingGit.setReleasesUAT(existingGit.getReleasesUAT());
            }

            githubRepository.save(existingGit);
        } else {
            throw new RuntimeException("Sonar Não encontrado");
        }
    }

    @Override
    public void deleteGit(Integer id) {
        Optional<GitHub> git = githubRepository.findById(id);

        if(git.isPresent()) {
            githubRepository.deleteById(id);
        }else {
            throw new IllegalArgumentException("GitHub não encontrado");
        }
    }

    @Override
    public List<GitHub> getGitHubs() {
        Sort sort  = Sort.by(Sort.Order.asc("nameApi").ignoreCase());
        return  githubRepository.findAll(sort);
    }

    @Override
    public List<GitHub> getGitHubsActiveProd() {
        Sort sort  = Sort.by(Sort.Order.asc("nameApi").ignoreCase());
        return githubRepository.findGitHubByActiveProd(true,sort);
    }
}
