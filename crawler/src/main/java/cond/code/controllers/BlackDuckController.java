package cond.code.controllers;

import cond.code.entities.BlackDuck;
import cond.code.repositories.BlackDuckRepository;
import cond.code.services.BlackDuckService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/blackduck")
public class BlackDuckController {
    String url;
    String env;

    private final BlackDuckService blackDuckService;

    public BlackDuckController(BlackDuckService blackDuckService) {
        this.blackDuckService = blackDuckService;
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
            List<BlackDuck> blackDucks = blackDuckService.getBlackDucks();
            return ResponseEntity.ok(blackDucks);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exeblack/{cookie}/{envv}")
    public ResponseEntity<String> executeBlackDuck(@PathVariable String cookie, @PathVariable String envv) {
        try {
            List<BlackDuck> blackDucks = blackDuckService.getBlackDucks();
            sonar(blackDucks,envv);

            return ResponseEntity.ok(url);

        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    private static void sonar(List<BlackDuck> blackDucks, String env) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Map<String, String> valueMap = new HashMap<>();
        for (BlackDuck blackDuck : blackDucks) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(blackDuck.getUrlApiBlackDuck()+"/"+env))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Processando URL: " + blackDuck.getUrlApiBlackDuck()+"/"+env);

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                // Use uma biblioteca JSON para analisar o JSON, por exemplo, org.json ou Gson
                // Aqui, para simplificação, vamos assumir que você tem um método para extrair valores do JSON
                Map<String, Object> jsonResponse = parseJson(responseBody);

                Map<String, Object> projectStatus = (Map<String, Object>) jsonResponse.get("categories");

                extractAndStoreValue(valueMap, projectStatus, "VULNERABILITY", "CRITICAL");
                extractAndStoreValue(valueMap, projectStatus, "VULNERABILITY", "HIGH");
                extractAndStoreValue(valueMap, projectStatus, "LICENSE", "HIGH");
                extractAndStoreValue(valueMap, projectStatus, "OPERATIONAL", "HIGH");
            } else {
                valueMap.put(blackDuck.getUrlApiBlackDuck()+"/"+env, "error");
                System.out.println("Erro ao acessar o site");
            }
        }

        writeExcel(valueMap);
    }
    private static Map<String, Object> parseJson(String json) {
        // Adicione aqui a lógica para parsear o JSON. Pode ser com org.json, Gson ou outra biblioteca JSON.
        // Retorne um mapa de exemplo para fins ilustrativos.
        return new HashMap<>();
    }
    private static void writeExcel(Map<String, String> valueMap) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("CDT");

        int colnum = 1;
        for (String key : valueMap.keySet()) {
            headerRow.createCell(colnum).setCellValue(key);
            colnum++;
        }

        Row dataRow = sheet.createRow(1);
        for (int i = 0; i < colnum; i++) {
            Cell cell = dataRow.createCell(i);
            cell.setCellValue(valueMap.getOrDefault(headerRow.getCell(i).getStringCellValue(), "error"));
        }

        try (FileOutputStream fileOut = new FileOutputStream("blackduck.xlsx")) {
            workbook.write(fileOut);
        }

        workbook.close();
    }
    private static void extractAndStoreValue(Map<String, String> valueMap, Map<String, Object> projectStatus, String category, String severity) {
        Map<String, Object> conditions = (Map<String, Object>) projectStatus.get(category);
        if (conditions != null) {
            String actualValue = (String) conditions.get(severity);
            valueMap.put(severity, actualValue != null ? actualValue : "error");
        } else {
            valueMap.put(severity, "error");
            System.out.println("Não encontrado");
        }
    }
}

