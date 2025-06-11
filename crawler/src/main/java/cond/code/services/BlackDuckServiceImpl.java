package cond.code.services;

import aj.org.objectweb.asm.commons.TryCatchBlockSorter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cond.code.entities.BlackDuck;
import cond.code.entities.Sonar;
import cond.code.repositories.BlackDuckRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BlackDuckServiceImpl implements BlackDuckService {

    private static List<BlackDuckData> blackDuckDataList = new ArrayList<>();
    private List<String> blackResult = new ArrayList<>();
    int progress = 0;
    int completedTasks = 0;
    private static List<String> blackduckExcel = new ArrayList<>();
    int totalTasks = 0;

    private static final Pattern VERSION_PATTERN = Pattern.compile("^releases/\\d+\\.\\d+(\\.\\d+)?$");

    private boolean isValidVersion(String versionName) {
        Matcher matcher = VERSION_PATTERN.matcher(versionName);
        return matcher.matches();
    }

    private static HttpClient createHttpClient() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
        }, new java.security.SecureRandom());

        SSLParameters sslParameters = new SSLParameters();
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        return HttpClient.newBuilder()
                .sslContext(sslContext)
                .sslParameters(sslParameters)
                .build();
    }

    @Autowired
    private BlackDuckRepository blackDuckRepository;

    @Override
    public void createBlackDuck(BlackDuck blackDuck) {
        if (blackDuckRepository.existsByNameApi(blackDuck.getNameApi())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nome da Api já existente (verifique se colocou )");
        }
        blackDuckRepository.save(blackDuck);
    }

    @Override
    public BlackDuck getBlackDuckId(Integer id) {
        Optional<BlackDuck> blackDuck = blackDuckRepository.findById(id);
        return blackDuck.orElseThrow(() -> new RuntimeException("BlackDuck com ID " + id + " não encontrado."));
    }

    @Override
    public BlackDuck getBlackDuckByApiName(String apiName) {
        Optional<BlackDuck> blackDuck = blackDuckRepository.findAllByNameApi(apiName);
        return blackDuck.orElseThrow(() -> new RuntimeException("BlackDuck com URL API " + apiName + " não encontrado."));
    }

    @Override
    public void updateBlackDuck(BlackDuck blackDuck) {
        if (blackDuckRepository.existsById(blackDuck.getIdBlackDuck())) {
            BlackDuck existingBlackDuck = blackDuckRepository.findById(blackDuck.getIdBlackDuck())
                    .orElseThrow(() -> new RuntimeException("BlackDuck Não encontrado"));

            if (blackDuck.getNameApi() != null && !blackDuck.getNameApi().isEmpty()) {
                existingBlackDuck.setNameApi(blackDuck.getNameApi());
            } else if (existingBlackDuck.getNameApi() == null) {
                existingBlackDuck.setNameApi(existingBlackDuck.getNameApi());
            }

            if (blackDuck.getUrlApi() != null && !blackDuck.getUrlApi().isEmpty()) {
                existingBlackDuck.setUrlApi(blackDuck.getUrlApi());
            } else if (existingBlackDuck.getUrlApi() == null) {
                existingBlackDuck.setUrlApi(existingBlackDuck.getUrlApi());
            }

            if (blackDuck.getType() != null && !blackDuck.getType().isEmpty()) {
                existingBlackDuck.setType(blackDuck.getType());
            } else if (existingBlackDuck.getType() == null) {
                existingBlackDuck.setType(existingBlackDuck.getType());
            }

            if (blackDuck.isActiveProd() != null) {
                existingBlackDuck.setActiveProd(blackDuck.isActiveProd());
            }

            if (blackDuck.getReleasesPROD() != null && !blackDuck.getReleasesPROD().isEmpty()) {
                existingBlackDuck.setReleasesPROD(blackDuck.getReleasesPROD());
            } else if (existingBlackDuck.getReleasesPROD() == null) {
                existingBlackDuck.setReleasesPROD(existingBlackDuck.getReleasesPROD());
            }
            if (blackDuck.getReleasesUAT() != null && !blackDuck.getReleasesUAT().isEmpty()) {
                existingBlackDuck.setReleasesUAT(blackDuck.getReleasesUAT());
            } else if (existingBlackDuck.getReleasesUAT() == null) {
                existingBlackDuck.setReleasesUAT(existingBlackDuck.getReleasesUAT());
            }

            blackDuckRepository.save(existingBlackDuck);
        } else {
            throw new RuntimeException("Sonar Não encontrado");
        }
    }
    @Override
    public void deleteBlackDuck(Integer id) {
        Optional<BlackDuck> blackDuck = blackDuckRepository.findById(id);

        if (blackDuck.isPresent()) {
            blackDuckRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("BlackDuck não encontrado");
        }
    }

    @Override
    public BlackDuck getBlackDuckByUrl(String url) {
        Optional<BlackDuck> blackDuck = blackDuckRepository.findByUrlApi(url);
        return blackDuck.orElseThrow(()-> new RuntimeException("BlackDuck com URL " + url + " não encontrado."));
    }

    @Override
    public List<BlackDuck> getBlackDucks() {
        Sort sort = Sort.by(Sort.Order.asc( "nameApi").ignoreCase());
        return blackDuckRepository.findAll(sort);
    }
    @Override
    public List<BlackDuck> getBlackDucksActiveProd() {
        Sort sort = Sort.by(Sort.Order.asc( "nameApi").ignoreCase());
        return blackDuckRepository.findAllByActiveProd(true,sort);
    }
    @Override
    public List<BlackDuck> getBlackDucksFront(){
        Sort sort = Sort.by(Sort.Order.asc("nameApi").ignoreCase());
        return blackDuckRepository.findAllByTypeEquals("front",sort);
    }
    @Override
    public List <BlackDuck> getBlackDucksFrontProd(){
        Sort sort = Sort.by(Sort.Order.asc("nameApi").ignoreCase());
        return blackDuckRepository.findAllByTypeEqualsAndActiveProd("front",true, sort);
    }
    @Override
    public List<BlackDuck> getBlackDucksBack(){
        Sort sort = Sort.by(Sort.Order.asc("nameApi").ignoreCase());
        return blackDuckRepository.findAllByTypeEquals("back",sort);
    }
    @Override
    public List <BlackDuck> getBlackDucksBackProd(){
        Sort sort = Sort.by(Sort.Order.asc("nameApi").ignoreCase());
        return blackDuckRepository.findAllByTypeEqualsAndActiveProd("back",true, sort);
    }



    public synchronized void updateProgress(int completedTasks) {
        progress = (int) ((double) completedTasks / totalTasks * 100);

    }
    private boolean processBlackEnvironment(HttpClient client, BlackDuck blackDuck, String cookieSonar, String cookieSonar2, String envv) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(blackDuck.getUrlApi().trim()))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                .header("Cookie", cookieSonar + " ;" + cookieSonar2)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray items = jsonResponse.optJSONArray("items");

            List<String> versions = new ArrayList<>();

            if (items != null) {
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String versionName = item.optString("versionName");
                    System.out.println("Checking versionName: " + versionName);

                    if (envv.trim().equals("releases/")) {
                        if (isValidVersion(versionName)) {
                            versions.add(versionName);
                        }
                    } else if(versionName.equals(envv.trim())){
                        versions.add(versionName);
                        blackduckExcel.add("main");

                    }
                }
            }

            if (!versions.isEmpty()) {
                String latestVersion = versions.stream()
                        .map(v -> v.replace("releases/", ""))
                        .map(v -> new Version(v))
                        .max(Comparator.naturalOrder())
                        .map(Version::toString)
                        .orElse(null);

                if (latestVersion != null) {
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        if (latestVersion.equals(item.optString("versionName").replace("releases/", ""))) {
                            JSONObject meta = item.optJSONObject("_meta");
                            if (meta != null) {
                                JSONArray links = meta.optJSONArray("links");
                                if (links != null) {
                                    for (int j = 0; j < links.length(); j++) {
                                        JSONObject link = links.getJSONObject(j);
                                        if ("riskProfile".equals(link.optString("rel"))) {
                                            String riskProfileLink = link.optString("href");
                                            blackduckExcel.add(latestVersion);

                                            blackResult.add(riskProfileLink);
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        blackduckExcel.add("Error");
        blackResult.add("https://blackduckorsomar/error/123error/321aaa");
        return false;
    }
    private boolean processBlackEnvironmentReleases(HttpClient client, BlackDuck blackDuck, String cookieSonar, String cookieSonar2, String envv, int b) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(blackDuck.getUrlApi().trim()))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                .header("Cookie", cookieSonar + " ;" + cookieSonar2)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray items = jsonResponse.optJSONArray("items");

            String latestVersion = "";
            if(b==2){
                latestVersion = blackDuck.getReleasesPROD();

            }
            if(b==3){
                latestVersion = blackDuck.getReleasesUAT();

            }

            if (latestVersion != null) {
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    if (latestVersion.equals(item.optString("versionName").replace("releases/", ""))) {
                        JSONObject meta = item.optJSONObject("_meta");
                        if (meta != null) {
                            JSONArray links = meta.optJSONArray("links");
                            if (links != null) {
                                for (int j = 0; j < links.length(); j++) {
                                    JSONObject link = links.getJSONObject(j);
                                    if ("riskProfile".equals(link.optString("rel"))) {
                                        String riskProfileLink = link.optString("href");
                                        blackResult.add(riskProfileLink);
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        blackResult.add("https://blackduckorsomar/error/123error/321aaa");
        return false;
    }



    public void foundyey(List<BlackDuck> blackDucks, String cookie, String cookie2, String envv, int a, int b) throws Exception {
        HttpClient client = createHttpClient();
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
        headers.put("Cookie", cookie + " ;" + cookie2);
        blackResult.clear();
        totalTasks =  blackDucks.size()*2;
        if(a==1){
            for (int i = 0; i < blackDucks.size(); i++) {
                BlackDuck blackDuck = blackDucks.get(i);
                processBlackEnvironment(client, blackDuck, cookie, cookie2, envv);

                completedTasks++;
                updateProgress(completedTasks);
                System.out.println("pt1 "+progress);
            }
            blackDuckDataList = processBlackDucks(blackResult, cookie, cookie2);
            try {
                writeExcel(blackDuckDataList, blackDucks,envv,b);
            } catch (Exception e) {
                try{
                    Thread.sleep(3000);

                }catch (InterruptedException aa){
                    aa.printStackTrace();
                }
            }
        }
        if(a==2){
            for (int i = 0; i < blackDucks.size(); i++) {
                BlackDuck blackDuck = blackDucks.get(i);
                processBlackEnvironmentReleases(client, blackDuck, cookie, cookie2, envv, b);

                completedTasks++;
                updateProgress(completedTasks);
                System.out.println("pt1 "+progress);

            }
            blackDuckDataList = processBlackDucks(blackResult, cookie, cookie2);

            try {
                writeExcel(blackDuckDataList, blackDucks,envv,b);
            } catch (Exception e) {
                try{
                    Thread.sleep(3000);

                }catch (InterruptedException aa){
                    aa.printStackTrace();
                }
            }

        }

        for (int i = 0;i < blackResult.size();i++) {
            System.out.println("pt2 "+ progress);
        }
        blackduckExcel.clear();

        try{
            Thread.sleep(3000);

        }catch (InterruptedException e){
            completedTasks=0;
            progress = 0;
            e.printStackTrace();

        }
        completedTasks=0;
        progress = 0;

    }

    public synchronized int getProgress() {
        return progress;
    }

    private List<BlackDuckServiceImpl.BlackDuckData> processBlackDucks(List<String> urls, String cookie, String cookie2) throws Exception {
        HttpClient client = createHttpClient();
        List<BlackDuckServiceImpl.BlackDuckData> blackDuckDataList = new ArrayList<>();

        for (int i = 0; i < urls.size() ;i++ ) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urls.get(i)))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .header("Cookie", cookie + " ;" + cookie2)
                    .build();

            BlackDuckServiceImpl.BlackDuckData blackDuckData = new BlackDuckServiceImpl.BlackDuckData(urls.get(i), 0, 0, 0, 0,0);

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("Processing URL: " + urls.get(i));

                if (response.statusCode() == 200) {
                    String responseBody = response.body();
                    Map<String, Object> jsonResponse = parseJson(responseBody);

                    Map<String, Object> projectStatus = (Map<String, Object>) jsonResponse.get("categories");

                    blackDuckData.setCriticalVulnerability(
                            extractValue(projectStatus, "VULNERABILITY", "CRITICAL"));
                    blackDuckData.setHighVulnerability(
                            extractValue(projectStatus, "VULNERABILITY", "HIGH"));
                    blackDuckData.setHighLicense(
                            extractValue(projectStatus, "LICENSE", "HIGH"));
                    blackDuckData.setMediumLicense(
                            extractValue(projectStatus, "LICENSE", "MEDIUM"));
                    blackDuckData.setHighOperational(
                            extractValue(projectStatus, "OPERATIONAL", "HIGH"));
                    completedTasks++;
                    updateProgress(completedTasks);
                } else {
                    completedTasks++;
                    updateProgress(completedTasks);
                    blackDuckData.setError();
                    System.out.println("Error accessing the site: HTTP " + response.statusCode());
                }
            } catch (IOException e) {
                completedTasks++;
                updateProgress(completedTasks);
                blackDuckData.setError();
                System.out.println("IOException while accessing the URL: " + urls.get(i));
                e.printStackTrace();
            } catch (InterruptedException e) {
                completedTasks++;
                updateProgress(completedTasks);
                blackDuckData.setError();
                System.out.println("Request was interrupted: " + urls.get(i));
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                completedTasks++;
                updateProgress(completedTasks);
                blackDuckData.setError();
                System.out.println("Unexpected error: " + urls.get(i));
                e.printStackTrace();
            }

            blackDuckDataList.add(blackDuckData);
        }
        return blackDuckDataList;
    }




    private static Map<String, Object> parseJson(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    }

    private static final String FILE_EXTENSION = ".xlsx";
    private static final String SHEET_NAME = "BlackDuck";

    public static void writeExcel(List<BlackDuckServiceImpl.BlackDuckData> blackDuckDataList, List<BlackDuck> blackDucks, String env, int b) throws IOException {
        File file = new File("Main" + FILE_EXTENSION);
        Workbook workbook;
        Sheet sheet;
        boolean fileExists = file.exists();

        if (fileExists) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(fileInputStream);
                sheet = workbook.getSheet(SHEET_NAME);
                if (sheet == null) {
                    sheet = workbook.createSheet(SHEET_NAME);
                }
            }
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet(SHEET_NAME);
        }

        int lastRowNum = sheet.getLastRowNum();
        int startRow = lastRowNum + 2;

        Row dateRow = sheet.createRow(startRow);
        LocalDate date = LocalDate.now();
        dateRow.createCell(0).setCellValue(date+" "+env);

        int headerRowIndex = startRow + 1;
        Row headerRow = sheet.createRow(headerRowIndex);

        int headerIndex = 0;
        if(b==1){
            for (int i = 0; i < blackDucks.size(); i++) {
                String apiName = blackDucks.get(i).getNameApi();
                headerRow.createCell(headerIndex++).setCellValue(apiName + " Branch");
                headerRow.createCell(headerIndex++).setCellValue(apiName + " Sec. Critical "+ blackduckExcel.get(i));
                headerRow.createCell(headerIndex++).setCellValue(apiName + " Sec. High "+ blackduckExcel.get(i));
                headerRow.createCell(headerIndex++).setCellValue(apiName + " License "+ blackduckExcel.get(i));
                headerRow.createCell(headerIndex++).setCellValue(apiName + " License M "+ blackduckExcel.get(i));
                headerRow.createCell(headerIndex++).setCellValue(apiName + " Operational "+ blackduckExcel.get(i));

            }

            Row dataRow = sheet.createRow(headerRowIndex + 1);
            int dataIndex = 0;
            int index =0;
            for (BlackDuckServiceImpl.BlackDuckData data : blackDuckDataList) {
                dataRow.createCell(dataIndex++).setCellValue(blackduckExcel.get(index));
                dataRow.createCell(dataIndex++).setCellValue(data.getCriticalVulnerability());
                dataRow.createCell(dataIndex++).setCellValue(data.getHighVulnerability());
                dataRow.createCell(dataIndex++).setCellValue(data.getHighLicense());
                dataRow.createCell(dataIndex++).setCellValue(data.getMediumLicense());
                dataRow.createCell(dataIndex++).setCellValue(data.getHighOperational());
                index++;
            }

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }
        }

        if(b==2){
            for (BlackDuck data : blackDucks) {
                String apiName = data.getNameApi()+" "+data.getReleasesPROD();
                headerRow.createCell(headerIndex++).setCellValue(apiName+" Branch");
                headerRow.createCell(headerIndex++).setCellValue(apiName + " Sec. Critical");
                headerRow.createCell(headerIndex++).setCellValue(apiName + " Sec. High");
                headerRow.createCell(headerIndex++).setCellValue(apiName + " License");
                headerRow.createCell(headerIndex++).setCellValue(apiName + " License M");
                headerRow.createCell(headerIndex++).setCellValue(apiName + " Operational");

            }

            Row dataRow = sheet.createRow(headerRowIndex + 1);
            int dataIndex = 0;
            int index = 0;
            for (BlackDuckServiceImpl.BlackDuckData data : blackDuckDataList) {
                dataRow.createCell(dataIndex++).setCellValue(blackduckExcel.get(index));
                dataRow.createCell(dataIndex++).setCellValue(data.getCriticalVulnerability());
                dataRow.createCell(dataIndex++).setCellValue(data.getHighVulnerability());
                dataRow.createCell(dataIndex++).setCellValue(data.getHighLicense());
                dataRow.createCell(dataIndex++).setCellValue(data.getMediumLicense());
                dataRow.createCell(dataIndex++).setCellValue(data.getHighOperational());
                index++;
            }

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }
        }

        if(b==3){
            for (BlackDuck data : blackDucks) {
                String apiName = data.getNameApi()+" "+data.getReleasesUAT();
                headerRow.createCell(headerIndex++).setCellValue(apiName+" Branch");
                headerRow.createCell(headerIndex++).setCellValue(apiName + " Sec. Critical");
                headerRow.createCell(headerIndex++).setCellValue(apiName + " Sec. High");
                headerRow.createCell(headerIndex++).setCellValue(apiName + " License");
                headerRow.createCell(headerIndex++).setCellValue(apiName + " License M");
                headerRow.createCell(headerIndex++).setCellValue(apiName + " Operational");

            }

            Row dataRow = sheet.createRow(headerRowIndex + 1);
            int dataIndex = 0;
            int index = 0;
            for (BlackDuckServiceImpl.BlackDuckData data : blackDuckDataList) {
                dataRow.createCell(dataIndex++).setCellValue(blackduckExcel.get(index));
                dataRow.createCell(dataIndex++).setCellValue(data.getCriticalVulnerability());
                dataRow.createCell(dataIndex++).setCellValue(data.getHighVulnerability());
                dataRow.createCell(dataIndex++).setCellValue(data.getHighLicense());
                dataRow.createCell(dataIndex++).setCellValue(data.getMediumLicense());
                dataRow.createCell(dataIndex++).setCellValue(data.getHighOperational());
                index++;
            }

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }
        }

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        } finally {

            workbook.close();
        }
    }

    private static int extractValue(Map<String, Object> projectStatus, String category, String severity) {
        Map<String, Object> conditions = (Map<String, Object>) projectStatus.get(category);
        if (conditions != null) {
            Object actualValue = conditions.get(severity);
            return (actualValue instanceof Integer) ? (Integer) actualValue : -999;
        }
        return -999;
    }

    static class BlackDuckData {
        private final String apiName;
        private Integer criticalVulnerability;
        private Integer highVulnerability;
        private Integer highLicense;

        private Integer mediumLicense;

        private Integer highOperational;

        public BlackDuckData(String apiName, Integer criticalVulnerability, Integer highVulnerability, Integer highLicense, Integer highOperational, Integer mediumLicense) {
            this.apiName = apiName;
            this.criticalVulnerability = criticalVulnerability != null ? criticalVulnerability : -999;
            this.highVulnerability = highVulnerability != null ? highVulnerability : -999;
            this.highLicense = highLicense != null ? highLicense : -999;
            this.highOperational = highOperational != null ? highOperational : -999;
            this.mediumLicense = mediumLicense != null ? mediumLicense : -999;
        }

        public String getApiName() {
            return apiName;
        }

        public Integer getCriticalVulnerability() {
            return criticalVulnerability;
        }

        public void setCriticalVulnerability(Integer criticalVulnerability) {
            this.criticalVulnerability = criticalVulnerability;
        }

        public Integer getHighVulnerability() {
            return highVulnerability;
        }

        public void setHighVulnerability(Integer highVulnerability) {
            this.highVulnerability = highVulnerability;
        }

        public Integer getHighLicense() {
            return highLicense;
        }

        public void setHighLicense(Integer highLicense) {
            this.highLicense = highLicense;
        }

        public Integer getHighOperational() {
            return highOperational;
        }

        public void setHighOperational(Integer highOperational) {
            this.highOperational = highOperational;
        }

        public Integer getMediumLicense() {
            return mediumLicense;
        }

        public void setMediumLicense(Integer mediumLicense) {
            this.mediumLicense = mediumLicense;
        }

        public void setError() {
            this.criticalVulnerability = -999;
            this.highVulnerability = -999;
            this.highLicense = -999;
            this.highOperational = -999;
            this.mediumLicense = -999;
        }
    }

    public class Version implements Comparable<BlackDuckServiceImpl.Version> {
        private String version;

        public Version(String version) {
            this.version = version;
        }

        @Override
        public int compareTo(BlackDuckServiceImpl.Version other) {
            String[] thisParts = this.version.split("\\.");
            String[] otherParts = other.version.split("\\.");

            int length = Math.max(thisParts.length, otherParts.length);

            for (int i = 0; i < length; i++) {
                int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
                int otherPart = i < otherParts.length ? Integer.parseInt(otherParts[i]) : 0;

                if (thisPart != otherPart) {
                    return Integer.compare(thisPart, otherPart);
                }
            }

            return 0;
        }

        @Override
        public String toString() {
            return version;
        }
    }


}