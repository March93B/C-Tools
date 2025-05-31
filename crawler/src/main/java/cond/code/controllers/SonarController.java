package cond.code.controllers;

import cond.code.entities.Sonar;
import cond.code.entities.SonarRequest;
import cond.code.services.SonarService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/sonar")
public class SonarController {
    private final SonarService sonarService;

    @GetMapping()
    public ModelAndView sonar() {
        ModelAndView mav = new ModelAndView("sonar");
        List<Sonar> sonars = sonarService.getsonars();
        mav.addObject("sonars", sonars);
        return mav;
    }


    public SonarController(SonarService sonarService){
        this.sonarService = sonarService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sonar> updateSonar(@RequestBody Sonar sonar, @PathVariable Integer id) {
        if (sonar == null || id == null) {
            return ResponseEntity.badRequest().body(null);
        }

        sonar.setIdSonar(id);
        try {
            sonarService.updateSonar(sonar);
            return ResponseEntity.ok(sonar);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSonar(@PathVariable Integer id){
        try {
            sonarService.deleteSonar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sonar> findSonarById(@PathVariable Integer id){
        try {
            Sonar sonar = sonarService.getSonarById(id);
            return ResponseEntity.ok(sonar);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Sonar> findSonarByName(@PathVariable String name){
        try {
            Sonar sonar = sonarService.getSonarByName(name);
            return ResponseEntity.ok(sonar);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/url/{url}")
    public ResponseEntity<Sonar> findSonarByUrl(@PathVariable String url){
        try {
            Sonar sonar = sonarService.getSonarByUrl(url);
            return ResponseEntity.ok(sonar);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @PostMapping("/exesonar/all")
    public ResponseEntity<Void> executeSonar(@RequestBody SonarRequest request) throws Exception {
        String cookie = request.getCookie();
        String cookie2 = request.getCookie2();
        String envv = request.getEnvv();
        int choice = request.getChoice();
        int releases = request.getRelease();
        int prodOnly = request.getProdOnly();
        List<Sonar> sonarList;

        if (prodOnly== 1){
            sonarList = sonarService.getsonarsActiveProd();


        }else{
            sonarList = sonarService.getsonars();

        }
        sonarService.updateProgress(0);

        if(choice==1){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    sonarService.sonar(sonarList, cookie, cookie2, envv,1,1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        if(choice==2){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    sonarService.sonar(sonarList, cookie, cookie2, envv,2,releases);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }


        return ResponseEntity.ok().build();
    }


    @PostMapping("/exesonar/front")
    public ResponseEntity<Void> executeFront(@RequestBody SonarRequest request) throws Exception {
        String cookie = request.getCookie();
        String cookie2 = request.getCookie2();
        String envv = request.getEnvv();
        int choice = request.getChoice();
        int releases = request.getRelease();
        int prodOnly = request.getProdOnly();
        sonarService.updateProgress(0);

        List<Sonar> sonarList;

        if (prodOnly== 1){
            sonarList = sonarService.getsonarsActiveProdBF("front");


        }else{
            sonarList = sonarService.getBF("front");

        }
        if(choice==1){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    sonarService.sonar(sonarList, cookie, cookie2, envv,1,1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        if(choice==2){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    sonarService.sonar(sonarList, cookie, cookie2, envv,2,releases);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/exesonar/back")
    public ResponseEntity<Void> executeBack(@RequestBody SonarRequest request) throws Exception {
        String cookie = request.getCookie();
        String cookie2 = request.getCookie2();
        String envv = request.getEnvv();
        int choice = request.getChoice();
        int releases = request.getRelease();
        int prodOnly = request.getProdOnly();
        sonarService.updateProgress(0);

        List<Sonar> sonarList;

        if (prodOnly == 1){
            sonarList = sonarService.getsonarsActiveProdBF("back");


        }else{
            sonarList = sonarService.getBF("back");

        }
        if(choice==1){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    sonarService.sonar(sonarList, cookie, cookie2, envv,1,1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        if(choice==2){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    sonarService.sonar(sonarList, cookie, cookie2, envv,2,releases);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/progress")
    public ResponseEntity<Integer> getProgress() {
        return ResponseEntity.ok(sonarService.getProgress());
    }


}
