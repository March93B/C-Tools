package march.controllers;
import march.entities.BlackDuck;
import march.entities.BlackDuckRequest;
import march.services.BlackDuckService;
import march.services.BlackDuckServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/blackduck")
public class BlackDuckController {

    private final BlackDuckService blackDuckService;

    public BlackDuckController(BlackDuckService blackDuckService, BlackDuckServiceImpl blackDuckServiceImpl) {
        this.blackDuckService = blackDuckService;
    }
    @GetMapping()
    public ModelAndView blackduck() {
        ModelAndView mave = new ModelAndView("blackduck");
        List<BlackDuck> blackducks = blackDuckService.getBlackDucks();
        mave.addObject("blackducks", blackducks);
        return mave;
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlackDuck> updateBlackDuck(@RequestBody BlackDuck blackDuck, @PathVariable Integer id) {
        if (blackDuck == null || id == null) {
            return ResponseEntity.badRequest().body(null);
        }

        blackDuck.setIdBlackDuck(id);
        try {
            blackDuckService.updateBlackDuck(blackDuck);
            return ResponseEntity.ok(blackDuck);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
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
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/exeblack")
    public ResponseEntity<String> executeBlackDuck(@RequestBody BlackDuckRequest request){
        String cookie = request.getCookie();
        String cookie2 = request.getCookie2();
        String envv = request.getEnvv();
        int releases = request.getRelease();
        int choice = request.getChoice();
        int prodOnly = request.getProdOnly();
        List<BlackDuck> blackDucks;
        if(prodOnly==1){

            blackDucks = blackDuckService.getBlackDucksActiveProd();
        }else{
            blackDucks = blackDuckService.getBlackDucks();
        }

        if(choice==1){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    blackDuckService.foundyey(blackDucks, cookie, cookie2, envv,1,1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        if(choice==2){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    blackDuckService.foundyey(blackDucks, cookie, cookie2, envv,2,releases);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }


        return ResponseEntity.ok().build();
    }
    @GetMapping("/progress")
    public ResponseEntity<Integer> getProgress() {
        return ResponseEntity.ok(blackDuckService.getProgress());
    }
}
