package com.is.restaurant;

import com.is.restaurant.model.Comanda;
import com.is.restaurant.model.Produs;
import com.is.restaurant.repository.ComandaRepository;
import com.is.restaurant.repository.ProdusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void filtreazaMeniuDupaOSinguraCategorie() throws Exception {
        MvcResult result = mockMvc.perform(get("/").param("categorie", "Aperitive"))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        List<Produs> produse = (List<Produs>) result.getModelAndView().getModel().get("listaProduse");

        assertEquals("Aperitive", produse.getFirst().getCategorie());
    }

    @Test
    void adaugaInCosSalveazaProdusulInSession() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/adauga-cos/1").session(session));

        @SuppressWarnings("unchecked")
        List<Produs> cos = (List<Produs>) session.getAttribute("cos");

        assertEquals(1, cos.size());
    }

    @Test
    void adaugaInCosProdusIndisponibilNuModificaCosul() throws Exception {
        Produs produs = produsRepository.findById(1L).orElseThrow();
        produs.setEsteDisponibil(false);
        produsRepository.save(produs);

        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/adauga-cos/1").session(session));

        assertNull(session.getAttribute("cos"));
    }

    @Test
    void actualizeazaStatusLaPreparare() throws Exception {
        Comanda comanda = comandaRepository.save(new Comanda());
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/login")
                .param("username", "ADMIN")
                .param("password", "adminRestaurantMagic12")
                .session(session));

        mockMvc.perform(post("/comenzi/" + comanda.getId() + "/status")
                .param("status", "Preparare")
                .session(session));

        assertEquals("Preparare", comandaRepository.findById(comanda.getId()).get().getStatus());
    }

    @Test
    void clientNeautentificatPoatePlasaComanda() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/adauga-cos/1").session(session));
        mockMvc.perform(post("/plaseaza-comanda").session(session));

        assertFalse(comandaRepository.findAll().isEmpty());
        assertNull(session.getAttribute("cos"));
    }
}
