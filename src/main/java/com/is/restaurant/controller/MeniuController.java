package com.is.restaurant.controller;

import com.is.restaurant.model.Produs;
import com.is.restaurant.repository.ProdusRepository;
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
    public String afiseazaMeniu(Model model) {
        List<Produs> produse = produsRepository.findAll();
        model.addAttribute("listaProduse", produse);
        return "meniu";
    }
}