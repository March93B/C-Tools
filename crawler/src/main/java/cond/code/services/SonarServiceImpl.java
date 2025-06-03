
package cond.code.services;

import cond.code.entities.Sonar;
import cond.code.repositories.SonarRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
public class SonarServiceImpl implements SonarService{

    private static final Pattern VERSION_PATTERN = Pattern.compile("^releases/\\d+\\.\\d+(\\.\\d+)*$");

    private boolean isValidVersion(String versionName) {
        Matcher matcher = VERSION_PATTERN.matcher(versionName);
        return matcher.matches();
    }

    int progress = 0;
    int totalTasks =0;
    private List<String> versionsExcel = new ArrayList<>();
    private List<Double> valueList =new ArrayList<>();

    @Autowired
    private SonarRepository sonarRepository;

    @Override
    public void createSonar(Sonar sonar) {
        sonarRepository.save(sonar);
    }

    @Override
    public void deleteSonar(Integer id) {
        Optional<Sonar> sonar = sonarRepository.findById(id);

        if(sonar.isPresent()) {
            sonarRepository.deleteById(id);
        }else {
            throw new IllegalArgumentException("Sonar não encontrado");
        }
    }

    @Override
    public void updateSonar(Sonar sonar) {
        if (sonarRepository.existsById(sonar.getIdSonar())) {
            Sonar existingSonar = sonarRepository.findById(sonar.getIdSonar())
                    .orElseThrow(() -> new RuntimeException("Sonar Não encontrado"));

            if (sonar.getNameApi() != null && !sonar.getNameApi().isEmpty()) {
                existingSonar.setNameApi(sonar.getNameApi());
            } else if (existingSonar.getNameApi() == null) {
                existingSonar.setNameApi(existingSonar.getNameApi());
            }

            if (sonar.getUrlApi() != null && !sonar.getUrlApi().isEmpty()) {
                existingSonar.setUrlApi(sonar.getUrlApi());
            } else if (existingSonar.getUrlApi() == null) {
                existingSonar.setUrlApi(existingSonar.getUrlApi());
            }

            if (sonar.isActiveProd() != null) {
                existingSonar.setActiveProd(sonar.isActiveProd());
            }

            if (sonar.getReleasesPROD() != null && !sonar.getReleasesPROD().isEmpty()) {
                existingSonar.setReleasesPROD(sonar.getReleasesPROD());

            } else if (existingSonar.getReleasesPROD() == null) {
                existingSonar.setReleasesPROD(existingSonar.getReleasesPROD());
            }
            if (sonar.getReleasesUAT() != null && !sonar.getReleasesUAT().isEmpty()) {
                existingSonar.setReleasesUAT(sonar.getReleasesUAT());
            } else if (existingSonar.getReleasesUAT() == null) {
                existingSonar.setReleasesUAT(existingSonar.getReleasesUAT());
            }

            sonarRepository.save(existingSonar);
        } else {
            throw new RuntimeException("Sonar não encontrado");
        }
    }
    @Override
    public Sonar getSonarById(Integer id) {
        Optional<Sonar> sonar = sonarRepository.findById(id);
        return sonar.orElseThrow(() -> new RuntimeException("Sonar com ID API " + id + " não encontrado."));

    }

    @Override
    public Sonar getSonarByName(String name) {
        Optional<Sonar> sonar = sonarRepository.findByNameApi(name);
        return sonar.orElseThrow(()-> new RuntimeException("Sonar com Nome API " + name + " não encontrado."));
    }

    @Override
    public Sonar getSonarByUrl(String url) {
        Optional<Sonar> sonar = sonarRepository.findByUrlApi(url);
        return sonar.orElseThrow(()-> new RuntimeException("Sonar com URL "+url+" não encontrado."));
    }

    @Override
    public List<Sonar> getsonars() {
        Sort sort = Sort.by(Sort.Order.asc("nameApi").ignoreCase());

        return sonarRepository.findAll(sort);
    }

    @Override
    public List<Sonar> getBF(String type) {
        Sort sort = Sort.by(Sort.Order.asc("nameApi").ignoreCase());

        return sonarRepository.findAllByTypeEquals(type, sort);
    }

    @Override
    public List<Sonar> getsonarsActiveProd() {
        Sort sort = Sort.by(Sort.Order.asc("nameApi").ignoreCase());

        return sonarRepository.findAllByActiveProd(true,sort);
    }

    @Override
    public List<Sonar> getsonarsActiveProdBF(String type) {
        Sort sort = Sort.by(Sort.Order.asc("nameApi").ignoreCase());

        return sonarRepository.findAllByTypeEqualsAndActiveProd(type, true,sort);
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
    @Override
    public synchronized void updateProgress(int completedTasks) {
        progress = (int) ((double) completedTasks / totalTasks * 100);
    }
    private static final String FILE_NAME = "Main.xlsx";
    private static final String SHEET_NAME = "Sonar";
    private boolean processLinkSonar(HttpClient client, Sonar sonar, String env, String cookieSonar, String cookieSonar2) throws IOException, InterruptedException {
        try {
            String apiMain = "";
            String urlBeforeApi = "";
            String[] parts = sonar.getUrlApi().trim().split("component=");

            if (parts.length > 1) {
                String componentPart = parts[1];
                apiMain = componentPart.split("&")[0];
            }

            int index = sonar.getUrlApi().trim().indexOf("/api");
            if (index != -1) {
                urlBeforeApi = sonar.getUrlApi().trim().substring(0, index);
                System.out.println("Parte da URL antes de '/api': " + urlBeforeApi);
            } else {
                System.out.println("'/api' não encontrado na URL.");
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBeforeApi + "/api/project_branches/list?project=" + apiMain.trim()))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .header("Cookie", cookieSonar + " ;" + cookieSonar2)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                JSONArray items = jsonResponse.optJSONArray("branches");
                List<String> versions = new ArrayList<>();

                if (items != null) {
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        String versionName = item.optString("name");

                        if (env.trim().equals("releases/")) {
                            if (isValidVersion(versionName)) {
                                versions.add(versionName);
                            }
                        } else {
                            processSonarEnvironment(client,sonar,env,cookieSonar,cookieSonar2);
                            versionsExcel.add("main");
                            return true;
                        }
                    }
                }

                if (!versions.isEmpty()) {
                    String latestVersion = versions.stream()
                            .map(v -> v.replace("releases/", ""))
                            .map(Version::new)
                            .max(Comparator.naturalOrder())
                            .map(Version::toString)
                            .orElse(null);

                    if (latestVersion != null) {
                        String latestRelease = "releases/" + latestVersion;
                        processSonarEnvironment(client, sonar, latestRelease, cookieSonar, cookieSonar2);
                        versionsExcel.add(latestVersion);
                        return true;
                    }else{
                        versionsExcel.add("error");
                        processSonarEnvironment(client, sonar, "error404", cookieSonar, cookieSonar2);
                        return false;
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            versionsExcel.add("error");
            processSonarEnvironment(client, sonar, "error404", cookieSonar, cookieSonar2);
            return false;
        }

        processSonarEnvironment(client, sonar, "error404", cookieSonar, cookieSonar2);
        versionsExcel.add("error");
        return false;
    }

    private boolean processLinkSonarReleases(HttpClient client, Sonar sonar, String cookieSonar, String cookieSonar2,int b) throws IOException, InterruptedException {
        try {
            String apiMain = "";
            String urlBeforeApi = "";
            String[] parts = sonar.getUrlApi().trim().split("component=");

            if (parts.length > 1) {
                String componentPart = parts[1];
                apiMain = componentPart.split("&")[0];
            } else {
            }

            int index = sonar.getUrlApi().trim().indexOf("/api");
            if (index != -1) {
                urlBeforeApi = sonar.getUrlApi().trim().substring(0, index);
            } else {
                System.out.println("'/api' não encontrado na URL.");
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBeforeApi + "/api/project_branches/list?project=" + apiMain.trim()))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .header("Cookie", cookieSonar + " ;" + cookieSonar2)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {

                String latestVersion = "";

                if(b==2){
                    latestVersion = sonar.getReleasesPROD();
                    versionsExcel.add(latestVersion);
                }

                if(b==3){
                    latestVersion = sonar.getReleasesUAT();
                    versionsExcel.add(latestVersion);
                }

                if (latestVersion != null) {
                    String latestRelease = "releases/" + latestVersion;
                    processSonarEnvironment(client, sonar, latestRelease, cookieSonar, cookieSonar2);
                    return true;
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            processSonarEnvironment(client, sonar, "error404", cookieSonar, cookieSonar2);
            return false;
        }

        processSonarEnvironment(client, sonar, "error404", cookieSonar, cookieSonar2);
        return false;
    }


    @Override
    public void sonar(List<Sonar> sonars, String cookieSonar, String cookieSonar2, String envv,int a, int b) throws Exception {
        HttpClient client = createHttpClient();
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
        headers.put("Cookie", cookieSonar + " ;" + cookieSonar2);
        valueList.clear();
        if(a == 1){
            for (int i = 0; i < sonars.size(); i++) {
                Sonar sonar = sonars.get(i);
                processLinkSonar(client, sonar, envv, cookieSonar, cookieSonar2);
                totalTasks = sonars.size();
                updateProgress(i + 1);
            }
            updateExcelFile(sonars, valueList, envv,a);

        }
        if(a == 2){
            for (int i = 0; i < sonars.size(); i++) {
                Sonar sonar = sonars.get(i);
                processLinkSonarReleases(client, sonar, cookieSonar, cookieSonar2,b);
                totalTasks = sonars.size();
                updateProgress(i + 1);

            }
            updateExcelFile(sonars, valueList, envv,b);

        }

        versionsExcel.clear();
        System.out.println("AGORA TEM Q IMORUMIR");
        System.out.println(valueList);
//        System.out.println(test);
//        updateProgress(100);
    }

    private boolean processSonarEnvironment(HttpClient client, Sonar sonar, String env, String cookieSonar, String cookieSonar2) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(sonar.getUrlApi().trim() + env.trim()))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .header("Cookie", cookieSonar + " ;" + cookieSonar2)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                JSONObject projectStatus = jsonResponse.optJSONObject("component");
                JSONArray conditions = projectStatus != null ? projectStatus.optJSONArray("measures") : new JSONArray();

                for (int i = 0; i < conditions.length(); i++) {
                    JSONObject condition = conditions.getJSONObject(i);
                    if ("coverage".equals(condition.optString("metric"))) {
                        double actualValue = condition.optDouble("value", Double.NaN);
                        Double aux = actualValue / 100;
                        valueList.add(aux);
//                        ValuesSonar valuesSonar = new ValuesSonar();
//                        valuesSonar.setValue(aux);
//                        valuesSonar.setSonar(sonar);
//                        valuesSonarService.createValue(valuesSonar);
                        return true;
                    }
                }
            }
        } catch (IOException | InterruptedException | JSONException e) {
            e.printStackTrace();
            valueList.add(-999.9);
        }
        valueList.add(-999.9);
        return false;
    }
    @Override
    public synchronized int getProgress() {
        return progress;
    }
    private void updateExcelFile(List<Sonar> sonars, List<Double> valueList, String env,int b) throws IOException {
        File file = new File(FILE_NAME);
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

        Row headerRow = sheet.createRow(startRow + 1);
        int colNum = 0;

        if(b==1){
            dateRow.createCell(0).setCellValue(env.toLowerCase() + " " + date);

            for (int i = 0; i < sonars.size(); i++) {
                headerRow.createCell(colNum).setCellValue(sonars.get(i).getNameApi()+" "+versionsExcel.get(i));
                headerRow.createCell(colNum+1).setCellValue(sonars.get(i).getNameApi()+" "+versionsExcel.get(i));
                colNum+=2;
            }

        }
        if(b==2){
            dateRow.createCell(0).setCellValue("PROD" + " " + date);
            for (Sonar sonar : sonars) {
                headerRow.createCell(colNum).setCellValue(sonar.getNameApi()+" "+sonar.getReleasesPROD());
                colNum++;
            }

        }
        if(b==3){
            dateRow.createCell(0).setCellValue("UAT" + " " + date);
            for (Sonar sonar : sonars) {
                headerRow.createCell(colNum).setCellValue(sonar.getNameApi()+" "+sonar.getReleasesUAT());
                colNum++;
            }

        }

        Row dataRow = sheet.createRow(startRow + 2);

        CellStyle percentageStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        percentageStyle.setDataFormat(format.getFormat("0.00%"));
        System.out.println("AKI "+ colNum);
        int count =0;
        int count2 =0;
        for (int i = 0; i < colNum; i++) {
            Cell cell = dataRow.createCell(i);
            if (count2<= valueList.size()) {
                double value = valueList.get(count);
                if (i % 2==0 || i == 0) {
                    cell.setCellValue(versionsExcel.get(count2));
                    count2++;

                }else{
                    cell.setCellValue(value);
                    cell.setCellStyle(percentageStyle);
                    count++;

                }
            } else {
                cell.setCellValue("Error");
            }
        }

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }
    }
    public class Version implements Comparable<SonarServiceImpl.Version> {
        private String version;

        public Version(String version) {
            this.version = version;
        }

        @Override
        public int compareTo(SonarServiceImpl.Version other) {
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