package com.is.restaurant;

import com.is.restaurant.model.Comanda;
import com.is.restaurant.model.Produs;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class ProiectTeste {

    @Test
    void testCalculeazaTotalCosGol() {
        Comanda comanda = new Comanda();
        comanda.calculeazaTotal();

        assertEquals(0.0, comanda.getTotalPlata());
    }

    @Test
    void testCalculeazaTotalUnProdus() {
        Comanda comanda = new Comanda();
        List<Produs> produse = new ArrayList<>();

        produse.add(new Produs("Apă plată", "Băuturi nespirtoase", 8.0, "Apă", false, true));
        comanda.setProduse(produse);
        comanda.calculeazaTotal();

        assertEquals(8.0, comanda.getTotalPlata());
    }

    @Test
    void testCalculeazaTotalMaiMulteProduse() {
        Comanda comanda = new Comanda();
        List<Produs> produse = new ArrayList<>();

        produse.add(new Produs("Apă", "Băuturi", 8.0, "Apă", false, true));
        produse.add(new Produs("Pesto", "Aperitive", 22.0, "Pâine, busuioc", false, true));
        comanda.setProduse(produse);

        comanda.calculeazaTotal();

        assertEquals(30.0, comanda.getTotalPlata());
    }

    @Test
    void testStatusInitialComanda() {
        Comanda comanda = new Comanda();

        assertEquals("În așteptare", comanda.getStatus());
    }

    @Test
    void testConstructorProdusDisponibilitate() {
        Produs produs = new Produs("Limonadă", "Băuturi", 15.0, "Lămâie", false, true);

        assertTrue(produs.isEsteDisponibil());
    }
}