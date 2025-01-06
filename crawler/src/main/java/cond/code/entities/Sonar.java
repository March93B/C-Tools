package cond.code.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Sonar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSonar;
    @Column(name = "apiNameSonar")
    private String nameApi;

    @Column(name = "apiUrlSonar")
    private String urlApi;

    @Column(name = "type")
    private String type;

    @Column(name = "releasesPROD")
    private String releasesPROD ;

    @Column(name = "releasesUAT")
    private String releasesUAT ;

    @Column(name = "activeProd")
    private Boolean activeProd;
    public Sonar(Integer idSonar, String nameApi, String urlApi, String type,String releasesPROD, String releasesUAT, Boolean activeProd) {
        this.idSonar = idSonar;
        this.nameApi = nameApi;
        this.urlApi = urlApi;
        this.type = type;
        this.releasesPROD = releasesPROD;
        this.releasesUAT = releasesUAT;
        this.activeProd = activeProd;
    }

    public Sonar() {

    }

    public Boolean isActiveProd() {
        return activeProd;
    }

    public void setActiveProd(Boolean activeProd) {
        this.activeProd = activeProd;
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

    public Integer getIdSonar() {
        return idSonar;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIdSonar(Integer idSonar) {
        this.idSonar = idSonar;
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

}
