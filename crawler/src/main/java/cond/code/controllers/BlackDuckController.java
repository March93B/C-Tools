package cond.code.controllers;

import cond.code.entities.BlackDuck;
import cond.code.repositories.BlackDuckRepository;
import cond.code.services.BlackDuckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blackduck")
public class BlackDuckController {

    private final BlackDuckService blackDuckService;
    private final BlackDuckRepository blackDuckRepository;

    public BlackDuckController(BlackDuckService blackDuckService, BlackDuckRepository blackDuckRepository) {
        this.blackDuckService = blackDuckService;
        this.blackDuckRepository = blackDuckRepository;
    }


    @PostMapping
    public ResponseEntity<BlackDuck> createBlackDuck(@RequestBody BlackDuck blackDuck) {
        try {
            blackDuckService.createBlackDuck(blackDuck);
            return ResponseEntity.ok(blackDuck);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlackDuck> updateBlackDuck(@PathVariable Integer id, @RequestBody BlackDuck blackDuck) {
        blackDuck.setIdBlackDuck(id);
        try {
            blackDuckService.updateBlackDuck(blackDuck);
            return ResponseEntity.ok(blackDuck);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlackDuck(@PathVariable Integer id) {
        try {
            blackDuckService.deleteBlackDuck(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlackDuck> getBlackDuck(@PathVariable Integer id) {
        try {
            BlackDuck blackDuck = blackDuckService.getBlackDuckId(id);
            return ResponseEntity.ok(blackDuck);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{apiName}")
    public ResponseEntity<BlackDuck> getBlackDuckByApiName(@PathVariable String apiName) {
        try {
            BlackDuck blackDuck = blackDuckService.getBlackDuckByApiName(apiName);
            return ResponseEntity.ok(blackDuck);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/url/{url}")
    public ResponseEntity<BlackDuck> getBlackDuckByUrl(@PathVariable String url) {
        try {
            BlackDuck blackduck = blackDuckService.getBlackDuckByUrl(url);
            return ResponseEntity.ok(blackduck);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<BlackDuck>> getAllBlackDuck() {
        try {
            List<BlackDuck> blackDucks = blackDuckRepository.findAll();
            return ResponseEntity.ok(blackDucks);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}

