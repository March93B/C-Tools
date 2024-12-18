package cond.code.controllers;

import cond.code.entities.BlackDuck;
import cond.code.entities.GitHub;
import cond.code.entities.Sonar;
import cond.code.services.BlackDuckService;
import cond.code.services.GitHubService;
import cond.code.services.SonarService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CreateController {

    @GetMapping("/create")
    public String create() {
        return "create";
    }
    private final BlackDuckService blackDuckService;
    private final SonarService sonarService;
    private final GitHubService gitHubService;
    public CreateController(SonarService sonarService, BlackDuckService blackDuckService, GitHubService gitHubService){
        this.sonarService = sonarService;
        this.blackDuckService = blackDuckService;
        this.gitHubService = gitHubService;
    }



    @PostMapping("/postsonar")
    public ResponseEntity<Sonar> createSonar(@RequestParam String nameApi,
                                             @RequestParam String urlApi) {
        try {
            Sonar sonar = new Sonar();
            sonar.setNameApi(nameApi);
            sonar.setUrlApi(urlApi);
            sonar.setType("back");
            sonarService.createSonar(sonar);

            return ResponseEntity.ok(sonar);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/postblack")
    public ResponseEntity<BlackDuck> createBlack(@RequestParam String nameApi,
                                             @RequestParam String urlApi) {
        try {
            BlackDuck blackDuck = new BlackDuck();
            blackDuck.setNameApi(nameApi);
            blackDuck.setUrlApi(urlApi);
            blackDuck.setType("back");
            blackDuckService.createBlackDuck(blackDuck);

            return ResponseEntity.ok(blackDuck);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/front/postsonar")
    public ResponseEntity<Sonar> createSonarFront(@RequestParam String nameApi,
                                             @RequestParam String urlApi) {
        try {
            Sonar sonar = new Sonar();
            sonar.setNameApi(nameApi);
            sonar.setUrlApi(urlApi);
            sonar.setType("front");
            sonarService.createSonar(sonar);

            return ResponseEntity.ok(sonar);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @PostMapping("/front/postblack")
    public ResponseEntity<BlackDuck> createBlackFront(@RequestParam String nameApi,
                                                 @RequestParam String urlApi) {
        try {
            BlackDuck blackDuck = new BlackDuck();
            blackDuck.setNameApi(nameApi);
            blackDuck.setUrlApi(urlApi);
            blackDuck.setType("front");
            blackDuckService.createBlackDuck(blackDuck);

            return ResponseEntity.ok(blackDuck);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @PostMapping("/git/front")
    public ResponseEntity<GitHub> createGitBack(@RequestParam String nameApi,
                                            @RequestParam String urlApi) {
        try {
            GitHub gitHub = new GitHub();
            gitHub.setNameApi(nameApi);
            gitHub.setUrlApi(urlApi);
            gitHub.setType("front");
            gitHubService.createGit(gitHub);

            return ResponseEntity.ok(gitHub);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @PostMapping("/git")
    public ResponseEntity<GitHub> createGitFront(@RequestParam String nameApi,
                                                @RequestParam String urlApi) {
        try {
            GitHub gitHub = new GitHub();
            gitHub.setNameApi(nameApi);
            gitHub.setUrlApi(urlApi);
            gitHub.setType("back");
            gitHubService.createGit(gitHub);

            return ResponseEntity.ok(gitHub);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
