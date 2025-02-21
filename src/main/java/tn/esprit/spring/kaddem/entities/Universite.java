package tn.esprit.spring.kaddem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;

@Entity
public class Universite implements Serializable{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer idUniv;
    private String nomUniv;
    private Integer anneeCreation;
    private Double budget;
    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Departement> departements;
    public Universite() {
        // TODO Auto-generated constructor stub
    }

    public Universite(String nomUniv) {
        super();
        this.nomUniv = nomUniv;
    }

    public Universite(String nomUniv, Integer anneeCreation, Double budget) {
        this.nomUniv = nomUniv;
        this.anneeCreation = anneeCreation;
        this.budget = budget;
    }

    public Set<Departement> getDepartements() {
        return departements;
    }

    public void setDepartements(Set<Departement> departements) {
        this.departements = departements;
    }

    public Integer getIdUniv() {
        return idUniv;
    }
    public void setIdUniv(Integer idUniv) {
        this.idUniv = idUniv;
    }
    public String getNomUniv() {
        return nomUniv;
    }
    public void setNomUniv(String nomUniv) {
        this.nomUniv = nomUniv;
    }

    public Integer getAnneeCreation() {
        return anneeCreation;
    }

    public void setAnneeCreation(Integer anneeCreation) {
        this.anneeCreation = anneeCreation;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }
}
