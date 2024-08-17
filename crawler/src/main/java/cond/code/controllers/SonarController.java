package cond.code.controllers;

import cond.code.entities.Sonar;
import cond.code.services.SonarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sonar")
public class SonarController {

    private final SonarService sonarService;

    public SonarController(SonarService sonarService){
        this.sonarService = sonarService;
    }
    @PostMapping()
    public ResponseEntity<Sonar> createSonar(@RequestBody Sonar sonar){
       try {
           sonarService.createSonar(sonar);
           return ResponseEntity.ok(sonar);
       }catch (Exception e){
           return ResponseEntity.badRequest().body(null);
       }
    }
    @PutMapping("/{id}")
    public ResponseEntity<Sonar> updateSonar(@RequestBody Sonar sonar, @PathVariable Integer id){
        sonar.setIdSonar(id);
        try {
            sonarService.updateSonar(sonar);
            return ResponseEntity.ok(sonar);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Sonar> deleteSonar(@PathVariable Integer id){
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
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping("name/{name}")
    public ResponseEntity<Sonar> findSonarByName(@PathVariable String name){
        try {
            Sonar sonar = sonarService.getSonarByName(name);
            return ResponseEntity.ok(sonar);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping("url/{url}")
    public ResponseEntity<Sonar> findSonarByUrl(@PathVariable String url){
        try {
            Sonar sonar = sonarService.getSonarByUrl(url);
            return ResponseEntity.ok(sonar);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }
}
