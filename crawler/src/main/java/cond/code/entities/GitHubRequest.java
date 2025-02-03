package cond.code.entities;


public class GitHubRequest {
    private String Cookie;
    private String Token;
    private String env;
    private int choice;
    private int releases;
    private int prodOnly;

    public int getProdOnly() {
        return prodOnly;
    }

    public void setProdOnly(int prodOnly) {
        this.prodOnly = prodOnly;
    }

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }

    public int getReleases() {
        return releases;
    }

    public void setReleases(int releases) {
        this.releases = releases;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getCookie() {
        return Cookie;
    }

    public void setCookie(String cookie) {
        Cookie = cookie;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
