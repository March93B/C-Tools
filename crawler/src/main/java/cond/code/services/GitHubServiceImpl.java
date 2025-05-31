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
