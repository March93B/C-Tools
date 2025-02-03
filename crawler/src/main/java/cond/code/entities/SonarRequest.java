package cond.code.entities;

public class SonarRequest {
    private String cookie;
    private String cookie2;
    private String envv;
    private int choice;
    private int release;
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

    public int getRelease() {
        return release;
    }

    public void setRelease(int release) {
        this.release = release;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getCookie2() {
        return cookie2;
    }

    public void setCookie2(String cookie2) {
        this.cookie2 = cookie2;
    }

    public String getEnvv() {
        return envv;
    }

    public void setEnvv(String envv) {
        this.envv = envv;
    }

}
