package cond.code.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class BlackDuck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBlackDuck;

    @Column(name = "nameApiBlackDuck")
    private String nameApi ;

    @Column(name = "urlApiBlackDuck")
    private String urlApi ;

    @Column(name = "type")
    private String type ;

    @Column(name = "releasesPROD")
    private String releasesPROD ;

    @Column(name = "releasesUAT")
    private String releasesUAT ;

    @Column(name = "activeProd")
    private Boolean activeProd;


    public BlackDuck(Integer idBlackDuck, String nameApi, String urlApi, String type,
                     String releasesPROD, String releasesUAT,Boolean activeProd) {
        this.idBlackDuck = idBlackDuck;
        this.nameApi = nameApi;
        this.urlApi = urlApi;
        this.type = type;
        this.releasesPROD = releasesPROD;
        this.releasesUAT = releasesUAT;
        this.activeProd = activeProd;
    }

    public BlackDuck() {

    }

    public Boolean isActiveProd() {
        return activeProd;
    }

    public void setActiveProd(Boolean activeProd) {
        this.activeProd = activeProd;
    }

    public String getReleasesUAT() {
        return releasesUAT;
    }

    public void setReleasesUAT(String releasesUAT) {
        this.releasesUAT = releasesUAT;
    }

    public String getReleasesPROD() {
        return releasesPROD;
    }

    public void setReleasesPROD(String releasesPROD) {
        this.releasesPROD = releasesPROD;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIdBlackDuck() {
        return idBlackDuck;
    }

    public void setIdBlackDuck(Integer idBlackDuck) {
        this.idBlackDuck = idBlackDuck;
    }

    public String getNameApi() {
        return nameApi;
    }

    public void setNameApi(String NameApi) {
        this.nameApi = NameApi;
    }

    public String getUrlApi() {
        return urlApi;
    }

    public void setUrlApi(String urlApi) {
        this.urlApi = urlApi;
    }

}
