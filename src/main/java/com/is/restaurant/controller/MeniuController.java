package com.is.restaurant.controller;

import com.is.restaurant.model.Produs;
import com.is.restaurant.repository.ProdusRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MeniuController {

    @Autowired
    private ProdusRepository produsRepository;

    @GetMapping("/")
    public String afiseazaMeniu(Model model, HttpSession session) {
        List<Produs> produse = produsRepository.findAll();
        model.addAttribute("listaProduse", produse);

        Object loggedInUser = session.getAttribute("loggedInUser");
        Object isAdminAttribute = session.getAttribute("isAdmin");

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("isLoggedIn", loggedInUser != null);

        model.addAttribute("isAdmin", Boolean.TRUE.equals(isAdminAttribute));

        return "meniu";
    }
}