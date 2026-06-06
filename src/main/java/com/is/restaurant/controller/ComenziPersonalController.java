package com.is.restaurant.controller;

import com.is.restaurant.model.Comanda;
import com.is.restaurant.repository.ComandaVizualizareRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ComenziPersonalController {

    @Autowired
    private ComandaVizualizareRepository comandaVizualizareRepository;

    @GetMapping("/comenzi")
    public String afiseazaComenzi(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        List<Comanda> comenzi = comandaVizualizareRepository.findAllByOrderByIdDesc();

        model.addAttribute("comenzi", comenzi);
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        model.addAttribute("isAdmin", Boolean.TRUE.equals(session.getAttribute("isAdmin")));

        return "comenzi";
    }
}