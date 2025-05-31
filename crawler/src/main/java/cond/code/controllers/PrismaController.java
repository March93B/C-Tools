
package cond.code.controllers;

import cond.code.entities.GitHub;
import cond.code.entities.GitHubRequest;
import cond.code.services.GitHubService;
import cond.code.services.PrismaCloudService;
import cond.code.services.PrismaCloudServiceImpl;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@RestController
@RequestMapping("/prisma")
public class PrismaController {

    private final GitHubService gitHubService;
    private final PrismaCloudService prismaCloudService;
    public PrismaController(GitHubService gitHubService, PrismaCloudService prismaCloudService ) {
        this.gitHubService = gitHubService;
        this.prismaCloudService = prismaCloudService;
    }

    @GetMapping("/gits")
    public ResponseEntity<List<GitHub>> getGits() {
        List<GitHub> gitHubs = gitHubService.getGitHubs();
        return ResponseEntity.ok(gitHubs);
    }

    @GetMapping()
    public ModelAndView github() {
        ModelAndView mave = new ModelAndView("prisma");
        List<GitHub> gitHubs = gitHubService.getGitHubs();
        mave.addObject("githubs", gitHubs);
        return mave;
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
            prismaCloudService.getLatestReleaseBranch(gitHubRequest.getToken(), gitHubs);

            prismaCloudService.getVulnerabilities(gitHubRequest.getToken(), gitHubs, gitHubRequest.getCookie(),release);

        }
        if(choice ==2){
            prismaCloudService.getLatestReleaseBranchProdUAT(gitHubs,release);

            prismaCloudService.getVulnerabilities(gitHubRequest.getToken(), gitHubs, gitHubRequest.getCookie(),release);

        }

        System.out.println("Execução finalizada.");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/progress")
    public ResponseEntity<Integer> getProgress() {
        return ResponseEntity.ok(prismaCloudService.getProgress());
    }

}
