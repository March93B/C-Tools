package cond.code.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class ValuesBlackDuck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "valores")
    private String value;

    @Column(name = "datetime" )
    private LocalDate date = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "black_duck_id")
    private BlackDuck blackDuck;

    public ValuesBlackDuck() {
    }

    public ValuesBlackDuck(Integer id, String value, LocalDate date) {
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BlackDuck getBlackDuck() {
        return blackDuck;
    }

    public void setBlackDuck(BlackDuck blackDuck) {
        this.blackDuck = blackDuck;
    }
}
