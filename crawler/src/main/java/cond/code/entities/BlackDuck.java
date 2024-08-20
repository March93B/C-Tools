package cond.code.entities;

import jakarta.persistence.*;

@Entity
public class BlackDuck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBlackDuck;
    @Column(name = "nameApiBlackDuck")
    private String nameApiBlackDuck ;

    @Column(name = "urlApiBlackDuck")
    private String urlApiBlackDuck ;

    public BlackDuck(Integer idBlackDuck, String nameApiBlackDuck, String urlApiBlackDuck) {
        this.idBlackDuck = idBlackDuck;
        this.nameApiBlackDuck = nameApiBlackDuck;
        this.urlApiBlackDuck = urlApiBlackDuck;
    }
    public BlackDuck() {

    }

    public Integer getIdBlackDuck() {
        return idBlackDuck;
    }

    public void setIdBlackDuck(Integer idBlackDuck) {
        this.idBlackDuck = idBlackDuck;
    }

    public String getNameApiBlackDuck() {
        return nameApiBlackDuck;
    }

    public void setNameApiBlackDuck(String nameApiBlackDuck) {
        this.nameApiBlackDuck = nameApiBlackDuck;
    }

    public String getUrlApiBlackDuck() {
        return urlApiBlackDuck;
    }

    public void setUrlApiBlackDuck(String urlApiBlackDuck) {
        this.urlApiBlackDuck = urlApiBlackDuck;
    }
}
