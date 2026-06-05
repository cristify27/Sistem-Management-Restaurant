package com.is.restaurant.controller;

import com.is.restaurant.model.Comanda;
import com.is.restaurant.model.Produs;
import com.is.restaurant.repository.ComandaRepository;
import com.is.restaurant.repository.ProdusRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CosController {

    @Autowired
    private ProdusRepository produsRepository;

    @Autowired
    private ComandaRepository comandaRepository;

    // Adaugă un produs în coșul stocat în sesiune
    @PostMapping("/adauga-cos/{id}")
    public String adaugaInCos(@PathVariable Long id, HttpSession session) {
        Produs produs = produsRepository.findById(id).orElse(null);
        if (produs != null) {
            // Luăm coșul curent din sesiune, sau creăm unul nou dacă nu există
            List<Produs> cos = (List<Produs>) session.getAttribute("cos");
            if (cos == null) {
                cos = new ArrayList<>();
            }
            cos.add(produs);
            session.setAttribute("cos", cos); // Salvăm înapoi în sesiune
        }
        return "redirect:/"; // Îl trimitem înapoi la meniu după ce adaugă
    }

    // Afișează pagina cu coșul de cumpărături
    @GetMapping("/cos")
    public String afiseazaCos(HttpSession session, Model model) {
        List<Produs> cos = (List<Produs>) session.getAttribute("cos");
        if (cos == null) {
            cos = new ArrayList<>();
        }

        double total = cos.stream().mapToDouble(Produs::getPret).sum();

        model.addAttribute("produseCos", cos);
        model.addAttribute("totalPlata", total);
        return "cos";
    }

    // Transformă coșul din sesiune într-o Comandă reală în baza de date
    @PostMapping("/plaseaza-comanda")
    public String plaseazaComanda(HttpSession session) {
        List<Produs> cos = (List<Produs>) session.getAttribute("cos");

        if (cos != null && !cos.isEmpty()) {
            Comanda comandaNoua = new Comanda();
            comandaNoua.setProduse(new ArrayList<>(cos)); // Adăugăm produsele
            comandaNoua.calculeazaTotal();
            comandaNoua.setStatus("În așteptare");

            comandaRepository.save(comandaNoua); // Salvăm în baza de date

            session.removeAttribute("cos"); // Golim coșul după comandă
        }
        return "redirect:/?comandaPlasata=true";
    }
}