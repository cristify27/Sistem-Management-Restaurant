package com.is.restaurant.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comenzi")
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status = "În așteptare";

    private int timpEstimare;

    private double totalPlata;

    private String metodaPlata;

    private String numarChitanta;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "comanda_produse",
            joinColumns = @JoinColumn(name = "comanda_id"),
            inverseJoinColumns = @JoinColumn(name = "produs_id")
    )
    private List<Produs> produse = new ArrayList<>();

    public Comanda() {}

    public void calculeazaTotal() {
        this.totalPlata = produse.stream().mapToDouble(Produs::getPret).sum();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getTimpEstimare() { return timpEstimare; }
    public void setTimpEstimare(int timpEstimare) { this.timpEstimare = timpEstimare; }

    public double getTotalPlata() { return totalPlata; }
    public void setTotalPlata(double totalPlata) { this.totalPlata = totalPlata; }

    public String getMetodaPlata() { return metodaPlata; }
    public void setMetodaPlata(String metodaPlata) { this.metodaPlata = metodaPlata; }

    public String getNumarChitanta() { return numarChitanta; }
    public void setNumarChitanta(String numarChitanta) { this.numarChitanta = numarChitanta; }

    public List<Produs> getProduse() { return produse; }
    public void setProduse(List<Produs> produse) { this.produse = produse; }
}