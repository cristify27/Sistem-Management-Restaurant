package com.is.restaurant.model;

import jakarta.persistence.*;

@Entity
@Table(name = "produse")
public class Produs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String denumire;

    @Column(nullable = false)
    private String categorie; // Ex: "Aperitive", "Feluri principale", "Băuturi spirtoase", "Băuturi nespirtoase"

    private double pret;

    @Column(length = 500)
    private String ingrediente;

    private boolean estePicant;
    private boolean esteVegetarian;

    private boolean esteDisponibil = true;

    public Produs() {}

    public Produs(String denumire, String categorie, double pret, String ingrediente, boolean estePicant, boolean esteVegetarian) {
        this.denumire = denumire;
        this.categorie = categorie;
        this.pret = pret;
        this.ingrediente = ingrediente;
        this.estePicant = estePicant;
        this.esteVegetarian = esteVegetarian;
        this.esteDisponibil = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDenumire() { return denumire; }
    public void setDenumire(String denumire) { this.denumire = denumire; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public double getPret() { return pret; }
    public void setPret(double pret) { this.pret = pret; }

    public String getIngrediente() { return ingrediente; }
    public void setIngrediente(String ingrediente) { this.ingrediente = ingrediente; }

    public boolean isEstePicant() { return estePicant; }
    public void setEstePicant(boolean estePicant) { this.estePicant = estePicant; }

    public boolean isEsteVegetarian() { return esteVegetarian; }
    public void setEsteVegetarian(boolean esteVegetarian) { this.esteVegetarian = esteVegetarian; }

    public boolean isEsteDisponibil() { return esteDisponibil; }
    public void setEsteDisponibil(boolean esteDisponibil) { this.esteDisponibil = esteDisponibil; }
}