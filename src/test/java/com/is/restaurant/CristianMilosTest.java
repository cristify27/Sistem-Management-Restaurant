package com.is.restaurant;

import com.is.restaurant.model.Comanda;
import com.is.restaurant.model.Produs;
import com.is.restaurant.repository.ComandaRepository;
import com.is.restaurant.repository.ProdusRepository;
import com.is.restaurant.repository.UtilizatorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Set de teste Cristian Milos*
 *   -clasa Comanda + calculeazaTotal()
 *   -login cu username + add-personal
 *   -acces /comenzi pentru staff (admin/ospatar)
 *   -plata: clientul plaseaza fara plata, ospatarul confirma
 *   -timp estimat la preluarea comenzii
 *   -adaugare produse doar admin
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CristianMilosTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ComandaRepository comandaRepository;

    @Autowired
    private ProdusRepository produsRepository;

    @Autowired
    private UtilizatorRepository utilizatorRepository;

    private MockHttpSession loginCaAdmin() throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                        .param("username", "ADMIN")
                        .param("password", "adminRestaurantMagic12"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        return (MockHttpSession) result.getRequest().getSession();
    }

    private MockHttpSession loginCuUsername(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();

        return (MockHttpSession) result.getRequest().getSession();
    }

    private Comanda comandaCuProduse() {
        Produs produs = produsRepository.findAll().getFirst();

        Comanda comanda = new Comanda();
        comanda.setProduse(new ArrayList<>(List.of(produs)));
        comanda.calculeazaTotal();
        comanda.setStatus("În așteptare");

        return comandaRepository.save(comanda);
    }

    // --- Comanda (model creat de mine) ---

    @Test
    void comandaCalculeazaTotalDinPreturileProduselor() {
        Produs produs1 = new Produs("Test 1", "Aperitive", 10.0, "ing", false, true);
        Produs produs2 = new Produs("Test 2", "Aperitive", 15.5, "ing", false, true);

        Comanda comanda = new Comanda();
        comanda.setProduse(List.of(produs1, produs2));
        comanda.calculeazaTotal();

        assertEquals(25.5, comanda.getTotalPlata());
    }

    // --- Fix rol OSPATAR + login cu username ---

    @Test
    void addPersonalSalveazaUtilizatorCuRolOspatar() throws Exception {
        MockHttpSession sessionAdmin = loginCaAdmin();

        mockMvc.perform(post("/add-personal")
                        .param("email", "ospatar.test@restaurant.null")
                        .param("username", "OSP_TEST")
                        .param("password", "ospatar123")
                        .session(sessionAdmin))
                .andExpect(status().is3xxRedirection());

        var ospatar = utilizatorRepository.findByUsernameAndPassword("OSP_TEST", "ospatar123");
        assertTrue(ospatar.isPresent());
        assertEquals("OSPATAR", ospatar.get().getRole());
    }

    @Test
    void ospatarSeAutentificaCuUsernameSiAcceseazaComenzile() throws Exception {
        MockHttpSession sessionAdmin = loginCaAdmin();

        mockMvc.perform(post("/add-personal")
                        .param("email", "ospatar2@restaurant.null")
                        .param("username", "OSP_TEST2")
                        .param("password", "ospatar123")
                        .session(sessionAdmin))
                .andExpect(status().is3xxRedirection());

        MockHttpSession sessionOspatar = loginCuUsername("OSP_TEST2", "ospatar123");

        mockMvc.perform(get("/comenzi").session(sessionOspatar))
                .andExpect(status().isOk());
    }

    // --- Refactor plată: client plasează fără plată ---

    @Test
    void plaseazaComandaNuSeteazaMetodaPlataSiChitanta() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/adauga-cos/1").session(session))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/plaseaza-comanda").session(session))
                .andExpect(status().is3xxRedirection());

        Comanda comanda = comandaRepository.findAll().getLast();
        assertEquals("În așteptare", comanda.getStatus());
        assertNull(comanda.getMetodaPlata());
        assertNull(comanda.getNumarChitanta());
    }

    @Test
    void confirmaPlataFaraStatusServitaNuEmiteChitanta() throws Exception {
        Comanda comanda = comandaCuProduse();
        MockHttpSession session = loginCaAdmin();

        mockMvc.perform(post("/comenzi/" + comanda.getId() + "/plata")
                        .param("metodaPlata", "Cash")
                        .session(session))
                .andExpect(status().is3xxRedirection());

        Comanda actualizata = comandaRepository.findById(comanda.getId()).orElseThrow();
        assertNull(actualizata.getNumarChitanta());
        assertNull(actualizata.getMetodaPlata());
    }

    @Test
    void confirmaPlataCuStatusServitaEmiteChitanta() throws Exception {
        Comanda comanda = comandaCuProduse();
        comanda.setStatus("Servită");
        comandaRepository.save(comanda);

        MockHttpSession session = loginCaAdmin();

        mockMvc.perform(post("/comenzi/" + comanda.getId() + "/plata")
                        .param("metodaPlata", "Card")
                        .session(session))
                .andExpect(status().is3xxRedirection());

        Comanda actualizata = comandaRepository.findById(comanda.getId()).orElseThrow();
        assertEquals("Card", actualizata.getMetodaPlata());
        assertNotNull(actualizata.getNumarChitanta());
        assertTrue(actualizata.getNumarChitanta().startsWith("CH-"));
    }

    // --- Timp estimat (feature adăugat de mine) ---

    @Test
    void seteazaTimpEstimareSalveazaMinutele() throws Exception {
        Comanda comanda = comandaCuProduse();
        MockHttpSession session = loginCaAdmin();

        mockMvc.perform(post("/comenzi/" + comanda.getId() + "/timp-estimat")
                        .param("timpEstimare", "25")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comenzi"));

        Comanda actualizata = comandaRepository.findById(comanda.getId()).orElseThrow();
        assertEquals(25, actualizata.getTimpEstimare());
    }

    // --- Securizare meniu: doar admin adaugă produse ---

    @Test
    void adaugaProdusFaraAdminRedirecteazaLaMeniu() throws Exception {
        mockMvc.perform(post("/adauga-produs")
                        .param("denumire", "Produs Test")
                        .param("categorie", "Aperitive")
                        .param("pret", "19.99")
                        .param("ingrediente", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
