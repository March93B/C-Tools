package cond.code.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class ValuesSonar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "valores")
    private Double value;

    @Column(name = "datetime" )
    private LocalDate date = LocalDate.now();

    @ManyToOne
    @JsonBackReference
    private Sonar sonar;

    public ValuesSonar() {
    }

    public ValuesSonar(Integer id, Double value, LocalDate date) {
        this.id = id;
        this.value = value;
        this.date = date != null ? date : LocalDate.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Sonar getSonar() {
        return sonar;
    }

    public void setSonar(Sonar sonar) {
        this.sonar = sonar;
    }
}
