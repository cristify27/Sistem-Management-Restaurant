package com.is.restaurant.controller;

import com.is.restaurant.model.Produs;
import com.is.restaurant.repository.ProdusRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MeniuController {

    @Autowired
    private ProdusRepository produsRepository;

    @GetMapping("/")
    public String afiseazaMeniu(@RequestParam(name = "categorie", required = false) List<String> categoriiSelectate, Model model, HttpSession session) {
        List<Produs> produse;

        if (categoriiSelectate != null && !categoriiSelectate.isEmpty()) {
            produse = produsRepository.findByCategorieIn(categoriiSelectate);
        } else {
            produse = produsRepository.findAll();
        }

        List<String> toateCategoriile = produsRepository.findAll().stream()
                .map(Produs::getCategorie)
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("listaProduse", produse);
        model.addAttribute("categorii", toateCategoriile);
        model.addAttribute("categoriiSelectate", categoriiSelectate);

        Object loggedInUser = session.getAttribute("loggedInUser");
        Object isAdminAttribute = session.getAttribute("isAdmin");
        Object isOspatarAttribute = session.getAttribute("isOspatar");

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("isLoggedIn", loggedInUser != null);
        model.addAttribute("isAdmin", Boolean.TRUE.equals(isAdminAttribute));
        model.addAttribute("isOspatar", Boolean.TRUE.equals(isOspatarAttribute));

        return "meniu";
    }

    // 1. Am securizat metoda de adăugare exclusiv pentru admin
    @PostMapping("/adauga-produs")
    public String adaugaProdus(
            @RequestParam String denumire,
            @RequestParam String categorie,
            @RequestParam double pret,
            @RequestParam String ingrediente,
            @RequestParam(required = false) boolean estePicant,
            @RequestParam(required = false) boolean esteVegetarian,
            HttpSession session) {

        Object isAdminAttribute = session.getAttribute("isAdmin");
        if (session.getAttribute("loggedInUser") == null || !Boolean.TRUE.equals(isAdminAttribute)) {
            return "redirect:/";
        }

        Produs produsNou = new Produs(denumire, categorie, pret, ingrediente, estePicant, esteVegetarian);
        produsRepository.save(produsNou);

        return "redirect:/";
    }

    @PostMapping("/modifica-produs/{id}")
    public String modificaProdus(
            @org.springframework.web.bind.annotation.PathVariable Long id,
            @RequestParam String denumire,
            @RequestParam String categorie,
            @RequestParam double pret,
            @RequestParam String ingrediente,
            @RequestParam(required = false) boolean estePicant,
            @RequestParam(required = false) boolean esteVegetarian,
            HttpSession session) {

        Object isAdminAttribute = session.getAttribute("isAdmin");
        if (session.getAttribute("loggedInUser") == null || !Boolean.TRUE.equals(isAdminAttribute)) {
            return "redirect:/";
        }

        produsRepository.findById(id).ifPresent(produs -> {
            produs.setDenumire(denumire);
            produs.setCategorie(categorie);
            produs.setPret(pret);
            produs.setIngrediente(ingrediente);
            produs.setEstePicant(estePicant);
            produs.setEsteVegetarian(esteVegetarian);

            produsRepository.save(produs);
        });

        return "redirect:/";
    }
}