package com.is.restaurant;

import com.is.restaurant.controller.LoginController;
import com.is.restaurant.model.Utilizator;
import com.is.restaurant.repository.UtilizatorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IonitaFabianLoginControllerTest {

    @Mock
    private UtilizatorRepository utilizatorRepository;

    @InjectMocks
    private LoginController loginController;

    private Utilizator creeazaUtilizator(Long id, String username, String password, String role) {
        Utilizator utilizator = new Utilizator();
        utilizator.setId(id);
        utilizator.setUsername(username);
        utilizator.setPassword(password);
        utilizator.setRole(role);
        return utilizator;
    }

    @Test
    @DisplayName("Pagina de login se afișează dacă utilizatorul nu este autentificat")
    void loginPage_utilizatorNeautentificat_returneazaPaginaLogin() {
        MockHttpSession session = new MockHttpSession();

        String rezultat = loginController.loginPage(session);

        assertEquals("login", rezultat);
    }

    @Test
    @DisplayName("Utilizatorul autentificat este redirecționat de la login către pagina principală")
    void loginPage_utilizatorAutentificat_redirectPaginaPrincipala() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", "Mircea");

        String rezultat = loginController.loginPage(session);

        assertEquals("redirect:/", rezultat);
    }

    @Test
    @DisplayName("Autentificarea corectă pentru ospătar setează sesiunea și redirecționează către meniu")
    void authenticate_dateCorecteOspatar_seteazaSesiunea() {
        MockHttpSession session = new MockHttpSession();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        Utilizator ospatar = creeazaUtilizator(2L, "Mircea", "123", "OSPATAR");

        when(utilizatorRepository.findByUsernameAndPassword("Mircea", "123"))
                .thenReturn(Optional.of(ospatar));

        String rezultat = loginController.authenticate("Mircea", "123", session, redirectAttributes);

        assertEquals("redirect:/", rezultat);
        assertEquals("Mircea", session.getAttribute("loggedInUser"));
        assertEquals(2L, session.getAttribute("loggedInUserId"));
        assertEquals(false, session.getAttribute("isAdmin"));

        verify(utilizatorRepository).findByUsernameAndPassword("Mircea", "123");
    }

    @Test
    @DisplayName("Autentificarea corectă pentru admin setează isAdmin true în sesiune")
    void authenticate_dateCorecteAdmin_seteazaRolAdmin() {
        MockHttpSession session = new MockHttpSession();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        Utilizator admin = creeazaUtilizator(1L, "ADMIN", "a123", "ADMIN");

        when(utilizatorRepository.findByUsernameAndPassword("ADMIN", "a123"))
                .thenReturn(Optional.of(admin));

        String rezultat = loginController.authenticate("ADMIN", "a123", session, redirectAttributes);

        assertEquals("redirect:/", rezultat);
        assertEquals("ADMIN", session.getAttribute("loggedInUser"));
        assertEquals(1L, session.getAttribute("loggedInUserId"));
        assertEquals(true, session.getAttribute("isAdmin"));

        verify(utilizatorRepository).findByUsernameAndPassword("ADMIN", "a123");
    }

    @Test
    @DisplayName("Autentificarea greșită redirecționează către login și nu setează sesiunea")
    void authenticate_dateGresite_redirectLoginFaraSesiune() {
        MockHttpSession session = new MockHttpSession();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        when(utilizatorRepository.findByUsernameAndPassword("gresit", "gresit"))
                .thenReturn(Optional.empty());

        String rezultat = loginController.authenticate("gresit", "gresit", session, redirectAttributes);

        assertEquals("redirect:/login", rezultat);
        assertNull(session.getAttribute("loggedInUser"));
        assertNull(session.getAttribute("loggedInUserId"));
        assertNull(session.getAttribute("isAdmin"));

        verify(utilizatorRepository).findByUsernameAndPassword("gresit", "gresit");
    }
}