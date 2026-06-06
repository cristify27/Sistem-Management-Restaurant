package com.is.restaurant.controller;

import com.is.restaurant.model.Utilizator;
import com.is.restaurant.repository.UtilizatorRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UtilizatorRepository utilizatorRepository;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String authenticate(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        return utilizatorRepository.findByUsernameAndPassword(username, password)
                .map(utilizator -> {
                    session.setAttribute("loggedInUser", utilizator.getUsername());
                    session.setAttribute("loggedInUserId", utilizator.getId());

                    session.setAttribute("isAdmin", "ADMIN".equalsIgnoreCase(utilizator.getRole()));

                    return "redirect:/";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("loginError", "Numele de utilizator sau parola este incorectă.");
                    return "redirect:/login";
                });
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/add-personal")
    public String arataFormularAddPersonal(HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/";
        }
        return "add-personal";
    }

    @PostMapping("/add-personal")
    public String salveazaPersonalInBD(@RequestParam String username,
                                       @RequestParam String password,
                                       @RequestParam String role,
                                       HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/";
        }

        Utilizator nouUtilizator = new Utilizator();
        nouUtilizator.setUsername(username);
        nouUtilizator.setPassword(password);
        nouUtilizator.setRole(role);

        utilizatorRepository.save(nouUtilizator);

        return "redirect:/";
    }
}