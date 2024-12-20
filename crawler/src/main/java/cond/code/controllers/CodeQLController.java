package cond.code.controllers;

import cond.code.entities.GitHub;
import cond.code.entities.GitHubRequest;
import cond.code.services.CodeQLService;
import cond.code.services.GitHubService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/codeql")
public class CodeQLController {
    private final CodeQLService codeQLService;
    private final GitHubService gitHubService;

    public CodeQLController(GitHubService gitHubService, CodeQLService codeQLService) {
        this.gitHubService = gitHubService;
        this.codeQLService = codeQLService;
    }
    @GetMapping()
    public ModelAndView github() {
        ModelAndView mave = new ModelAndView("codeql");
        List<GitHub> gitHubs = gitHubService.getGitHubs();
        mave.addObject("githubs", gitHubs);
        return mave;
    }
    @GetMapping("/gits")
    public ResponseEntity<List<GitHub>> getGits() {
        List<GitHub> gitHubs = gitHubService.getGitHubs();
        return ResponseEntity.ok(gitHubs);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteGitHub(@PathVariable Integer id) {
        try {
            gitHubService.deleteGit(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GitHub> updateGitHub(@PathVariable Integer id, @RequestBody GitHub gitHub) {
        if (id == null || gitHub == null){
            return ResponseEntity.badRequest().body(null);
        }
        gitHub.setIdGit(id);
        try {
            gitHubService.updateGit(gitHub);
            return ResponseEntity.ok(gitHub);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/exec")
    public ResponseEntity<Void> postGitHub(@RequestBody GitHubRequest gitHubRequest) throws Exception {
//        String token = "";
//        String cookie = "";
//        String envName = "main";
        int choice = gitHubRequest.getChoice();
        int release = gitHubRequest.getReleases();
        int prodOnly = gitHubRequest.getProdOnly();
        List<GitHub> gitHubs;

        if (prodOnly==1){
            gitHubs = gitHubService.getGitHubsActiveProd();
        }else{
            gitHubs = gitHubService.getGitHubs();
        }

        if(choice ==1){
            codeQLService.getLatestReleaseBranch(gitHubRequest.getToken(), gitHubs);

            codeQLService.getVulnerabilities(gitHubs, gitHubRequest.getCookie(),release);

        }
        if(choice ==2){
            codeQLService.getLatestReleaseBranchProdUAT(gitHubs,release);

            codeQLService.getVulnerabilities(gitHubs, gitHubRequest.getCookie(),release);

        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/progress")
    public ResponseEntity<Integer> getProgress() {
        return ResponseEntity.ok(codeQLService.getProgress());
    }

}
