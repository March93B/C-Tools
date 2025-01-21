package cond.code.entities;

import jakarta.persistence.*;

@Entity
public class Seeker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nameApiSeeker")
    private String nameApi ;

    @Column(name = "urlApiSeeker")
    private String urlApi ;

    @Column(name = "type")
    private String type ;

    @Column(name = "releasesPROD")
    private String releasesPROD ;

    @Column(name = "releasesUAT")
    private String releasesUAT ;

    @Column(name = "activeProd")
    private Boolean activeProd;

    public Seeker(Integer id, String nameApi, String urlApi, String type, String releasesPROD, String releasesUAT, Boolean activeProd) {
        this.id = id;
        this.nameApi = nameApi;
        this.urlApi = urlApi;
        this.type = type;
        this.releasesPROD = releasesPROD;
        this.releasesUAT = releasesUAT;
        this.activeProd = activeProd;
    }

    public Seeker() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameApi() {
        return nameApi;
    }

    public void setNameApi(String nameApi) {
        this.nameApi = nameApi;
    }

    public String getUrlApi() {
        return urlApi;
    }

    public void setUrlApi(String urlApi) {
        this.urlApi = urlApi;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReleasesPROD() {
        return releasesPROD;
    }

    public void setReleasesPROD(String releasesPROD) {
        this.releasesPROD = releasesPROD;
    }

    public String getReleasesUAT() {
        return releasesUAT;
    }

    public void setReleasesUAT(String releasesUAT) {
        this.releasesUAT = releasesUAT;
    }

    public Boolean getActiveProd() {
        return activeProd;
    }

    public void setActiveProd(Boolean activeProd) {
        this.activeProd = activeProd;
    }
}

