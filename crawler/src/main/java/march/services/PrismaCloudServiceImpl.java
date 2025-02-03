package march.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import march.entities.GitHub;
import march.repositories.GitHubRepository;
import org.apache.poi.ss.usermodel.*;
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
public class PrismaCloudServiceImpl implements PrismaCloudService {

    private final GitHubRepository githubRepository;
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
    public PrismaCloudServiceImpl(GitHubRepository githubRepository) {
        this.githubRepository = githubRepository;
        this.restTemplate = new RestTemplate();

    }
    List<String> result = new ArrayList<>();
    List<String> finalResult = new ArrayList<>();
    List<Integer> resultNum = new ArrayList<>();
    String repoOwner = "";

    private static final String GITHUB_API_URL = "https://api.github.com";

    private String prismaCloudJobName = "Build And Deploy / Prisma Cloud Scanning";

    private String prismaCloudJobName2 = "Prisma Cloud Scanning";

    private RestTemplate restTemplate;

    int progress = 0;
    int completedTasks = 0;
    int totalTasks = 0;

    public synchronized void updateProgress(int completedTasks) {
        progress = (int) ((double) completedTasks / totalTasks * 100);

    }
    @Override
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
                                .map(Version::new)
                                .max(Comparator.naturalOrder())
                                .map(Version::toString)
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
            Pattern patternCritical = Pattern.compile("Critical:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

            Pattern patternHigh = Pattern.compile("High....:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
//        Pattern patternMedium = Pattern.compile("Medium..:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
//        Pattern patternLow = Pattern.compile("Low.....:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
//        Pattern patternTotal = Pattern.compile("TOTAL...:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

            Matcher matcherCritical = patternCritical.matcher(doc.text());
            vulnerabilities.put("Critical", matcherCritical.find() ? Integer.parseInt(matcherCritical.group(1)) : 0);
            Matcher matcherHigh = patternHigh.matcher(doc.text());
            vulnerabilities.put("High", matcherHigh.find() ? Integer.parseInt(matcherHigh.group(1)) : 0);
//        Matcher matcherMedium = patternMedium.matcher(doc.text());
//        vulnerabilities.put("Medium", matcherMedium.find() ? Integer.parseInt(matcherMedium.group(1)) : 0);
//        Matcher matcherLow = patternLow.matcher(doc.text());
//        vulnerabilities.put("Low", matcherLow.find() ? Integer.parseInt(matcherLow.group(1)) : 0);
//
//        Matcher matcherTotal = patternTotal.matcher(doc.text());
//        vulnerabilities.put("TOTAL", matcherTotal.find() ? Integer.parseInt(matcherTotal.group(1)) : 0);

            return vulnerabilities;
        } catch (Exception e) {
            System.err.println("Error extracting vulnerabilities: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    private Map<String, Object> getJobDetails(String jobId, String repoName, String repoOwner, String githubToken) {
        try {
            String jobDetailsUrl = String.format("%s/repos/%s/%s/actions/jobs/%s", GITHUB_API_URL, repoOwner, repoName, jobId);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "token " + githubToken);
            headers.set("Accept", "application/vnd.github+json");
            ResponseEntity<Map> response = restTemplate.exchange(jobDetailsUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error fetching job details for jobId " + jobId + ": " + e.getMessage());
            return null;
        }
    }

    private String getPrismaCloudJobId(String branchName, String repoName, String repoOwner, String githubToken) {
        try {
            String workflowRunsUrl = String.format("%s/repos/%s/%s/actions/runs?branch=%s", GITHUB_API_URL, repoOwner, repoName, branchName);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "token " + githubToken);
            headers.set("Accept", "application/vnd.github+json");

            ResponseEntity<Map> response = restTemplate.exchange(workflowRunsUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to fetch workflow runs: " + response.getStatusCode());
            }

            List<Map> workflowRuns = (List<Map>) response.getBody().get("workflow_runs");
            if (workflowRuns == null || workflowRuns.isEmpty()) {
                System.err.println("No workflow runs found for branch " + branchName);
                return null;
            }

            Map<String, Object> latestRun = (Map<String, Object>) workflowRuns.get(0);
            Object runIdObj = latestRun.get("id");

            String runId = null;
            if (runIdObj instanceof Long) {
                runId = String.valueOf(runIdObj);
            } else if (runIdObj instanceof Integer) {
                runId = String.valueOf(runIdObj);
            } else if (runIdObj instanceof String) {
                runId = (String) runIdObj;
            } else {
                throw new IllegalArgumentException("Unexpected type for runId: " + runIdObj.getClass().getName());
            }

            if (runId != null) {
                String jobsUrl = String.format("%s/repos/%s/%s/actions/runs/%s/jobs", GITHUB_API_URL, repoOwner, repoName, runId);
                ResponseEntity<Map> jobsResponse = restTemplate.exchange(jobsUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

                if (jobsResponse.getStatusCode() != HttpStatus.OK) {
                    throw new RuntimeException("Failed to fetch job details: " + jobsResponse.getStatusCode());
                }

                List<Map> jobs = (List<Map>) jobsResponse.getBody().get("jobs");
                if (jobs != null) {
                    for (Map job : jobs) {
                        if (prismaCloudJobName.equals(job.get("name")) || prismaCloudJobName2.equals(job.get("name"))) {
                            Object jobIdObj = job.get("id");
                            if (jobIdObj instanceof Long) {
                                return String.valueOf(jobIdObj);
                            } else if (jobIdObj instanceof Integer) {
                                return String.valueOf(jobIdObj);
                            } else if (jobIdObj instanceof String) {
                                return (String) jobIdObj;
                            } else {
                                throw new IllegalArgumentException("Unexpected type for jobId: " + jobIdObj.getClass().getName());
                            }
                        }
                    }
                } else {
                    System.err.println("No jobs found in workflow run for repo: " + repoName + " branch: " + branchName);
                }
            } else {
                System.err.println("Run ID is null or invalid for branch: " + branchName);
            }
        } catch (Exception e) {
            System.err.println("Error occurred while fetching Prisma Cloud job ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void getVulnerabilities(String githubToken, List<GitHub> gitHubs, String cookieValue, int b) throws IOException {
        try {
            resultNum.clear();
            System.out.println("Iniciando busca de vulnerabilidades.");

            HttpClient client = createHttpClient();
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
            headers.put("Cookie", cookieValue);

            ObjectMapper objectMapper = new ObjectMapper();
            String jobId = "";
            for (int i = 0; i < gitHubs.size(); i++) {
                try {

                    System.out.println("Processando vulnerabilidades para o repositório: " + gitHubs.get(i).getUrlApi());

                    if (b == 0) {
                        jobId = getPrismaCloudJobId(result.get(i), gitHubs.get(i).getNameApi(), repoOwner, githubToken);
                    } if( b== 1) {
                        jobId = getPrismaCloudJobId("main", gitHubs.get(i).getNameApi(), repoOwner, githubToken);
                    }
                    if(b == 2) {
                        jobId = getPrismaCloudJobId(gitHubs.get(i).getReleasesPROD(), gitHubs.get(i).getNameApi(), repoOwner, githubToken);
                    }
                    if(b == 3) {
                        jobId = getPrismaCloudJobId(gitHubs.get(i).getReleasesUAT(), gitHubs.get(i).getNameApi(), repoOwner, githubToken);
                    }


                    if (jobId == null) {
                        finalResult.add(i, "Prisma Cloud job not found");
                        System.out.println("Prisma Cloud job não encontrado para o repositório: " + gitHubs.get(i).getUrlApi());
                        resultNum.add(-999);
                        resultNum.add(-999);
                        completedTasks++;
                        updateProgress(completedTasks);
                        continue;
                    }

                    Map<String, Object> jobDetails = getJobDetails(jobId, gitHubs.get(i).getNameApi(), repoOwner, githubToken);
                    if (jobDetails == null) {
                        finalResult.add(i, "Prisma Cloud job not found");
                        System.out.println("Job details não encontrados para o job ID: " + jobId);
                        resultNum.add(-999);
                        resultNum.add(-999);
                        completedTasks++;
                        updateProgress(completedTasks);
                        continue;
                    }

                    String jobHtmlUrl = (String) jobDetails.get("html_url");
                    System.out.println("URL do job HTML: " + jobHtmlUrl);

                    java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                            .uri(URI.create(jobHtmlUrl))
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                            .header("Cookie", cookieValue)
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        String htmlContent = response.body();
                        System.out.println("Conteúdo HTML recebido com sucesso.");
                        Map<String, Integer> vulnerabilities = extractVulnerabilitiesFromHtml(htmlContent);

                        for (String severity : Arrays.asList("Critical", "High")) {
                            resultNum.add(Integer.valueOf(String.valueOf(vulnerabilities.getOrDefault(severity, -999))));
                        }

                        try {
                            String jsonResponse = objectMapper.writeValueAsString(vulnerabilities);
                            finalResult.add(i, jsonResponse);
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
    private static final String SHEET_NAME = "Prisma";
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
            dateRow.createCell(0).setCellValue(date+" lastest releases");
            for (int i = 0; i < gitHubs.size(); i++) {
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " Crit " + result.get(i));
                colNum++;
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " High " + result.get(i));
                colNum++;

            }
        }

        if(b==1) {
            for (int i = 0; i < gitHubs.size(); i++) {
                dateRow.createCell(0).setCellValue(date+" main");
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " Crit " + "main");
                colNum++;
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " High " + "main");
                colNum++;
            }
        }
        if(b==2) {
            for (int i = 0; i < gitHubs.size(); i++) {
                dateRow.createCell(0).setCellValue(date+" PROD Releases");
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " Crit " +  gitHubs.get(i).getReleasesPROD());
                colNum++;
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " High " + gitHubs.get(i).getReleasesPROD());
                colNum++;

            }
        }
        if(b==3) {
            for (int i = 0; i < gitHubs.size(); i++) {
                dateRow.createCell(0).setCellValue(date+" UAT Releases");
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " Crit " +  gitHubs.get(i).getReleasesUAT());
                colNum++;
                headerRow.createCell(colNum).setCellValue(gitHubs.get(i).getNameApi() + " High " + gitHubs.get(i).getReleasesUAT());
                colNum++;

            }
        }
        Row dataRow = sheet.createRow(startRow + 2);


        for (int i = 0; i < colNum; i++) {
            Cell cell = dataRow.createCell(i);
            if (i < resultNum.size()) {
                double value = resultNum.get(i);
                cell.setCellValue(value);
            } else {
                cell.setCellValue("error");
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
    public static class Version implements Comparable<Version> {
        private String version;

        public Version(String version) {
            this.version = version;
        }

        @Override
        public int compareTo(Version other) {
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
