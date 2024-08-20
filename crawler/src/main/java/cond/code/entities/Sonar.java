package cond.code.entities;

import jakarta.persistence.*;

@Entity
public class Sonar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSonar;
    @Column(name = "apiNameSonar")
    private String apiNameSonar;

    @Column(name = "apiUrlSonar")
    private String apiUrlSonar;

    public Sonar(Integer idSonar, String apiNameSonar, String apiUrlSonar) {
        this.idSonar = idSonar;
        this.apiNameSonar = apiNameSonar;
        this.apiUrlSonar = apiUrlSonar;
    }

    public Sonar() {

    }

    public Integer getIdSonar() {
        return idSonar;
    }

    public void setIdSonar(Integer idSonar) {
        this.idSonar = idSonar;
    }

    public String getApiNameSonar() {
        return apiNameSonar;
    }

    public void setApiNameSonar(String apiNameSonar) {
        this.apiNameSonar = apiNameSonar;
    }

    public String getApiUrlSonar() {
        return apiUrlSonar;
    }

    public void setApiUrlSonar(String apiUrlSonar) {
        this.apiUrlSonar = apiUrlSonar;
    }
}
