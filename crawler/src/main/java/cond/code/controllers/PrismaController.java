
package cond.code.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cond.code.entities.GitHub;
import cond.code.entities.GitHubRequest;
import cond.code.services.GitHubServiceImpl;
import cond.code.services.PrismaCloudService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.client.RestTemplate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;

import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/prisma")
public class PrismaController {

    private final GitHubServiceImpl gitHubService;
    private final PrismaCloudService prismaCloudService;
    public PrismaController(GitHubServiceImpl gitHubService,PrismaCloudService prismaCloudService ) {
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
