package cond.code.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Ambiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAmbiente")
    private Integer idAmbiente;

    @Column(name = "nomeAmbiente")
    private String nomeAmbiente;

    @OneToMany
    private List<BlackDuck> blackDucks;

    public Ambiente(Integer idAmbiente, String nomeAmbiente) {
        this.idAmbiente = idAmbiente;
        this.nomeAmbiente = nomeAmbiente;
    }

    public Ambiente() {
    }

    public Integer getIdAmbiente() {
        return idAmbiente;
    }

    public void setIdAmbiente(Integer idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    public String getNomeAmbiente() {
        return nomeAmbiente;
    }

    public void setNomeAmbiente(String nomeAmbiente) {
        this.nomeAmbiente = nomeAmbiente;
    }


}
