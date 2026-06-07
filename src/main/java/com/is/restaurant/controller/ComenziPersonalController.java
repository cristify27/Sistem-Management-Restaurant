package com.is.restaurant.controller;

import com.is.restaurant.model.Comanda;
import com.is.restaurant.repository.ComandaVizualizareRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        boolean isAdmin = Boolean.TRUE.equals(session.getAttribute("isAdmin"));
        boolean isOspatar = Boolean.TRUE.equals(session.getAttribute("isOspatar"));

        if (!isAdmin && !isOspatar) {
            return "redirect:/";
        }

        List<Comanda> comenzi = comandaVizualizareRepository.findAllByOrderByIdDesc();

        model.addAttribute("comenzi", comenzi);
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isOspatar", isOspatar);

        return "comenzi";
    }

    @PostMapping("/comenzi/{id}/status")
    public String actualizeazaStatusComanda(@PathVariable Long id,
                                            @RequestParam String status,
                                            HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        boolean isAdmin = Boolean.TRUE.equals(session.getAttribute("isAdmin"));
        boolean isOspatar = Boolean.TRUE.equals(session.getAttribute("isOspatar"));
        if (!isAdmin && !isOspatar) return "redirect:/";

        Comanda comanda = comandaVizualizareRepository.findById(id).orElse(null);

        if (comanda != null) {
            if (status.equals("În așteptare") ||
                    status.equals("Preparare") ||
                    status.equals("Servită")) {

                comanda.setStatus(status);
                comandaVizualizareRepository.save(comanda);
            }
        }

        return "redirect:/comenzi";
    }

    @PostMapping("/comenzi/{id}/plata")
    public String confirmaPlataComanda(@PathVariable Long id,
                                       @RequestParam String metodaPlata,
                                       HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        boolean isAdmin = Boolean.TRUE.equals(session.getAttribute("isAdmin"));
        boolean isOspatar = Boolean.TRUE.equals(session.getAttribute("isOspatar"));
        if (!isAdmin && !isOspatar) return "redirect:/";

        Comanda comanda = comandaVizualizareRepository.findById(id).orElse(null);

        if (comanda != null) {
            if (metodaPlata.equals("Cash") || metodaPlata.equals("Card")) {
                comanda.setMetodaPlata(metodaPlata);

                if (comanda.getNumarChitanta() == null || comanda.getNumarChitanta().isBlank()) {
                    comanda.setNumarChitanta(genereazaNumarChitanta(comanda.getId()));
                }

                comandaVizualizareRepository.save(comanda);
            }
        }

        return "redirect:/comenzi";
    }

    private String genereazaNumarChitanta(Long idComanda) {
        return "CH-" + idComanda + "-" + System.currentTimeMillis();
    }
}