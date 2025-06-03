package cond.code.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import cond.code.entities.GitHub;
import cond.code.repositories.GitHubRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CodeQLServiceImpl implements CodeQLService {

    private RestTemplate restTemplate;

    private final GitHubRepository repo;

    public CodeQLServiceImpl(GitHubRepository repo) {
        this.repo = repo;
        this.restTemplate = new RestTemplate();

    }

    private static HttpClient createHttpClient() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new javax.net.ssl.TrustManager[] {
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        }, new java.security.SecureRandom());

        SSLParameters sslParameters = new SSLParameters();
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        return HttpClient.newBuilder()
                .sslContext(sslContext)
                .sslParameters(sslParameters)
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build();
    }

    List<String> result = new ArrayList<>();
    List<String> finalResult = new ArrayList<>();
    List<Integer> resultNum = new ArrayList<>();
    String repoOwner = "";

    private static final String GITHUB_API_URL = "https://api.github.com";

    private String prismaCloudJobName = "Build And Deploy / Prisma Cloud Scanning";

    private String prismaCloudJobName2 = "Prisma Cloud Scanning";


    int progress = 0;
    int completedTasks = 0;
    int totalTasks = 0;

    public synchronized void updateProgress(int completedTasks) {
        progress = (int) ((double) completedTasks / totalTasks * 100);

    }
    public synchronized int getProgress() {
        return progress;
    }
    @Override
    public void getLatestReleaseBranch(String githubToken, List<GitHub> gitHubs) {
        try {
            totalTasks = gitHubs.size() * 2;
            String repoName = gitHubs.get(0).getUrlApi();
            String[] parts = repoName.split("/");
            repoOwner = parts[3];

            for (GitHub gitHub : gitHubs) {
                try {
                    String url = String.format("%s/repos/%s/%s/branches?per_page=100", GITHUB_API_URL, repoOwner, gitHub.getNameApi());
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", "token " + githubToken);
                    headers.set("Accept", "application/vnd.github+json");
                    HttpEntity<String> entity = new HttpEntity<>(headers);
                    List<String> releaseBranches = new ArrayList<>();
                    boolean hasMorePages = true;
                    int page = 1;

                    while (hasMorePages) {
                        String paginatedUrl = url + "&page=" + page;
                        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                                paginatedUrl, HttpMethod.GET, entity,
                                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
                        );

                        if (response.getStatusCode() == HttpStatus.OK) {
                            for (Map<String, Object> branch : response.getBody()) {
                                String branchName = (String) branch.get("name");
                                System.out.println(gitHub.getNameApi() + " " + branchName);

                                if (isValidRelease(branchName)) {
                                    releaseBranches.add(branchName);
                                }
                            }

                            String linkHeader = response.getHeaders().getFirst("Link");
                            if (linkHeader != null && linkHeader.contains("rel=\"next\"")) {
                                hasMorePages = true;
                                page++;
                                System.out.println("Página " + page);
                            } else {
                                hasMorePages = false;
                            }
                        } else {
                            System.err.println("Erro ao recuperar as branches.");
                            result.add("Error");
                            completedTasks++;
                            updateProgress(completedTasks);
                            break;
                        }
                    }

                    if (!releaseBranches.isEmpty()) {
                        String latestVersion = releaseBranches.stream()
                                .map(this::extractVersion)
                                .map(CodeQLServiceImpl.Version::new)
                                .max(Comparator.naturalOrder())
                                .map(CodeQLServiceImpl.Version::toString)
                                .orElse(null);

                        if (latestVersion != null) {
                            System.out.println(gitHub.getNameApi());
                            result.add("releases/" + latestVersion);
                            completedTasks++;
                            updateProgress(completedTasks);
                        }
                    } else {
                        result.add("Error");
                        completedTasks++;
                        updateProgress(completedTasks);
                    }
                } catch (Exception e) {
                    System.err.println("Error");
                    result.add("Error");
                    completedTasks++;
                    updateProgress(completedTasks);
                }
            }
        } catch (Exception e) {
            result.add("Error");
            completedTasks++;
            updateProgress(completedTasks);
            System.err.println("Erro ao processar os repositórios: " + e.getMessage());
        }

        for (int i = 0; i < result.size(); i++) {
            System.out.println(gitHubs.get(i).getNameApi());
            System.out.println(result.get(i));
        }
    }
    @Override
    public void getLatestReleaseBranchProdUAT(List<GitHub> gitHubs, int b) {
        totalTasks = gitHubs.size();
        String repoName = gitHubs.get(0).getUrlApi();
        String[] parts = repoName.split("/");
        repoOwner = parts[3];

        if(b ==1){
            for (GitHub gitHub : gitHubs) {
                result.add("main");


            }
        }

        if(b ==2){
            for (GitHub gitHub : gitHubs) {
                result.add("releases/" + gitHub.getReleasesPROD());


            }
        }
        if(b ==3){
            for (GitHub gitHub : gitHubs) {
                result.add("releases/" + gitHub.getReleasesUAT());

            }
        }

    }
    private static final Pattern VERSION_PATTERN = Pattern.compile("^releases/\\d+\\.\\d+(\\.\\d+)*$");

    private boolean isValidRelease(String branchName) {
        Matcher matcher = VERSION_PATTERN.matcher(branchName);
        return matcher.matches();
    }

    private String extractVersion(String branchName) {
        Pattern versionPattern = Pattern.compile("^releases/(\\d+\\.\\d+(\\.\\d+)?)");
        Matcher matcher = versionPattern.matcher(branchName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
    private Map<String, Integer> extractVulnerabilitiesFromHtml(String htmlContent) {
        try {
            Map<String, Integer> vulnerabilities = new HashMap<>();
            Document doc = Jsoup.parse(htmlContent);

            Pattern patternOpen = Pattern.compile("(\\d+)\\s*Open", Pattern.CASE_INSENSITIVE);
            Pattern patternClosed = Pattern.compile("(\\d+)\\s*Closed", Pattern.CASE_INSENSITIVE);

            Matcher matcherOpen = patternOpen.matcher(doc.text());
            vulnerabilities.put("Open", matcherOpen.find() ? Integer.parseInt(matcherOpen.group(1)) : 0);

            Matcher matcherClosed = patternClosed.matcher(doc.text());
            vulnerabilities.put("Closed", matcherClosed.find() ? Integer.parseInt(matcherClosed.group(1)) : 0);

            return vulnerabilities;
        } catch (Exception e) {
            System.err.println("Error extracting vulnerabilities: " + e.getMessage());
            return Collections.emptyMap();
        }
    }
    @Override
    public void getVulnerabilities(List<GitHub> gitHubs, String cookieValue, int b) throws IOException {
        try {
            resultNum.clear();
            System.out.println("Iniciando busca de vulnerabilidades.");

            HttpClient client = createHttpClient();
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
            headers.put("Cookie", cookieValue);

            ObjectMapper objectMapper = new ObjectMapper();
            for (int i = 0; i < gitHubs.size(); i++) {
                try {
                    result.set(i, result.get(i).replace("/", "%2F"));

                    System.out.println("Processando vulnerabilidades para o repositório: " + gitHubs.get(i).getUrlApi());

                    if(b == 0){
                        finalResult.add(gitHubs.get(i).getUrlApi()+"/security/code-scanning?query=is%3Aopen+tool%3AcodeQL+branch%3A"+result.get(i));
                        System.out.println("URL do job HTML: " + finalResult.get(i));
                    }
                    if(b == 1){
                        finalResult.add(gitHubs.get(i).getUrlApi()+"/security/code-scanning?query=is%3Aopen+tool%3AcodeQL+branch%3Amain");
                        System.out.println("URL do job HTML: " + finalResult.get(finalResult.size()-1));
                        System.out.println(i);
                    }
                    if(b == 2){
                        finalResult.add(gitHubs.get(i).getUrlApi()+"/security/code-scanning?query=is%3Aopen+tool%3AcodeQL+branch%3A"
                                +"releases%2F"+gitHubs.get(i).getReleasesPROD().replace("/","%2F"));
                        System.out.println("URL do job HTML: " + finalResult.get(i));
                    }
                    if(b == 3){
                        finalResult.add(gitHubs.get(i).getUrlApi()+"/security/code-scanning?query=is%3Aopen+tool%3AcodeQL+branch%3A"
                                +"releases%2F"+gitHubs.get(i).getReleasesUAT().replace("/","%2F"));
                        System.out.println("URL do job HTML: " + finalResult.get(i));
                    }



                    java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                            .uri(URI.create(finalResult.get(finalResult.size()-1)))
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                            .header("Cookie", cookieValue)
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        String htmlContent = response.body();
                        System.out.println("Conteúdo HTML recebido com sucesso.");
                        Map<String, Integer> vulnerabilities = extractVulnerabilitiesFromHtml(htmlContent);

                        for (String severity : Arrays.asList("Open", "Closed")) {
                            resultNum.add(Integer.valueOf(String.valueOf(vulnerabilities.getOrDefault(severity, -999))));
                        }

                        try {
                            String jsonResponse = objectMapper.writeValueAsString(vulnerabilities);
                            finalResult.add(i,jsonResponse);
                            completedTasks++;
                            updateProgress(completedTasks);
                        } catch (Exception e) {
                            finalResult.add(i, "Erro ao converter mapa para JSON");
                            System.err.println("Erro ao converter vulnerabilidades para JSON: " + e.getMessage());
                            e.printStackTrace();
                            resultNum.add(-999);
                            resultNum.add(-999);
                            completedTasks++;
                            updateProgress(completedTasks);

                        }
                    } else {
                        finalResult.add(i, "Erro ao acessar a URL HTML do job. Código de status: " + response.statusCode());
                        System.err.println("Erro ao acessar a URL HTML do job. Código de status: " + response.statusCode());
                        resultNum.add(-999);
                        resultNum.add(-999);
                        completedTasks++;
                        updateProgress(completedTasks);

                    }
                } catch (Exception e) {
                    finalResult.add(i, "Erro ao obter vulnerabilidades para o repositório: " + gitHubs.get(i).getUrlApi());
                    System.err.println("Erro ao obter vulnerabilidades para o repositório " + gitHubs.get(i).getUrlApi() + ": " + e.getMessage());
                    e.printStackTrace();
                    resultNum.add(-999);
                    resultNum.add(-999);
                    completedTasks++;
                    updateProgress(completedTasks);

                }
            }

            System.out.println("Busca de vulnerabilidades finalizada.");
        } catch (Exception e) {
            completedTasks++;
            updateProgress(completedTasks);
            System.err.println("Erro ao processar vulnerabilidades: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Resultados finais:");
        for (String resultItem : finalResult) {
            System.out.println(resultItem);
        }
        for (Integer resultItem : resultNum) {
            System.out.println(resultItem);
        }


        try {
            updateExcelFile(gitHubs,b);
        } catch (Exception e) {
            try{
                Thread.sleep(3000);

            }catch (InterruptedException a){
                a.printStackTrace();
            }
        }

        try{
            Thread.sleep(3000);
            System.out.println("aaaa");
        }catch (InterruptedException a){
            a.printStackTrace();
        }

        completedTasks=0;
        progress = 0;
        totalTasks = 0;
        result.clear();
        finalResult.clear();

    }
    private static final String FILE_NAME = "Main.xlsx";
    private static final String SHEET_NAME = "CodeQL";
    private void updateExcelFile(List<GitHub> gitHubs, int b) throws IOException {
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
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println(gitHubs.size());
        System.out.println(result.size());
        int colNum = 0;
        if(b==0) {
            for (int i = 0; i < gitHubs.size(); i++) {
                dateRow.createCell(0).setCellValue(date+" lastest releases");
                headerRow.createCell(colNum).setCellValue(" Branch");
                colNum++;
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " Open " + result.get(i));
                colNum++;
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " Closed " + result.get(i));
                colNum++;

            }
        }

        if(b==1) {
            for (int i = 0; i < gitHubs.size(); i++) {
                dateRow.createCell(0).setCellValue(date+" main");
                headerRow.createCell(colNum).setCellValue(" Branch");
                colNum++;
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " Open " + "main");
                colNum++;
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " Closed " + "main");
                colNum++;
            }
        }
        if(b==2) {
            for (int i = 0; i < gitHubs.size(); i++) {
                dateRow.createCell(0).setCellValue(date+" PROD Releases");
                headerRow.createCell(colNum).setCellValue(" Branch");
                colNum++;
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " Open " +  gitHubs.get(i).getReleasesPROD());
                colNum++;
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " Closed " + gitHubs.get(i).getReleasesPROD());
                colNum++;

            }
        }
        if(b==3) {
            for (int i = 0; i < gitHubs.size(); i++) {
                dateRow.createCell(0).setCellValue(date+" UAT Releases");
                headerRow.createCell(colNum).setCellValue(" Branch");
                colNum++;
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " Open " +  gitHubs.get(i).getReleasesUAT());
                colNum++;
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " Closed " + gitHubs.get(i).getReleasesUAT());
                colNum++;

            }
        }
        Row dataRow = sheet.createRow(startRow + 2);

        int count = 0;
        int count2= 0;
        for (int i = 0; i < colNum; i++) {
            Cell cell = dataRow.createCell(i);
            if (i % 3 == 0){
                if (count<result.size()){
                    cell.setCellValue(result.get(count));
                    count++;
                }else{
                    cell.setCellValue("Error");
                }
            } else if (i% 3 ==1 || i % 3 == 2) {
                if (count2<resultNum.size()){
                    cell.setCellValue(resultNum.get(count2));
                    count2++;
                }else{
                    cell.setCellValue("Error");
                }
            }
        }

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        }
        finally {
            workbook.close();
        }

    }
    public static class Version implements Comparable<CodeQLServiceImpl.Version> {
        private String version;

        public Version(String version) {
            this.version = version;
        }

        @Override
        public int compareTo(CodeQLServiceImpl.Version other) {
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
