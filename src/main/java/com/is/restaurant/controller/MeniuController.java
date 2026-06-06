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

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("isLoggedIn", loggedInUser != null);
        model.addAttribute("isAdmin", Boolean.TRUE.equals(isAdminAttribute));

        return "meniu";
    }

    @PostMapping("/adauga-produs")
    public String adaugaProdus(
            @RequestParam String denumire,
            @RequestParam String categorie,
            @RequestParam double pret,
            @RequestParam String ingrediente,
            @RequestParam(required = false) boolean estePicant,
            @RequestParam(required = false) boolean esteVegetarian,
            HttpSession session) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        Produs produsNou = new Produs(denumire, categorie, pret, ingrediente, estePicant, esteVegetarian);
        produsRepository.save(produsNou);

        return "redirect:/";
    }
}