package com.is.restaurant;

import com.is.restaurant.controller.ComenziPersonalController;
import com.is.restaurant.controller.LoginController;
import com.is.restaurant.controller.MeniuController;
import com.is.restaurant.model.Comanda;
import com.is.restaurant.model.Produs;
import com.is.restaurant.repository.ComandaVizualizareRepository;
import com.is.restaurant.repository.UtilizatorRepository;
import com.is.restaurant.repository.ProdusRepository;

import org.junit.jupiter.api.BeforeEach;
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
class VrabieMihaiAlexandru {

	@Mock
	private ProdusRepository produsRepository;

	@Mock
	private UtilizatorRepository utilizatorRepository;

	@Mock
	private ComandaVizualizareRepository comandaVizualizareRepository;

	@InjectMocks
	private MeniuController meniuController;

	@InjectMocks
	private LoginController loginController;

	@InjectMocks
	private ComenziPersonalController comenziPersonalController;

	private MockHttpSession session;
	private RedirectAttributesModelMap redirectAttributes;

	@BeforeEach
	void setUp() {
		session = new MockHttpSession();
		redirectAttributes = new RedirectAttributesModelMap();
	}

	@Test
	@DisplayName("1. Adăugarea unui produs nou ca Admin - Verificare redirecționare corectă")
	void testAdaugareProdusNou_AdminAutentificat_RedirectMeniu() {
		session.setAttribute("loggedInUser", "SefRestaurant");
		session.setAttribute("isAdmin", true);

		String view = meniuController.adaugaProdus(
				"Burger Vita", "Fel principal", 45.0, "Chifla, Carne, Sos", false, false, session
		);

		assertEquals("redirect:/", view);
	}

	@Test
	@DisplayName("2. Verificare adăugare produs nou - Salvare efectivă în Repository")
	void testVerificareAdaugareProdusNou_ApeleazaProdusRepositorySave() {
		session.setAttribute("loggedInUser", "SefRestaurant");
		session.setAttribute("isAdmin", true);

		meniuController.adaugaProdus(
				"Limonada", "Bauturi", 15.0, "Lamaie, Apa, Miere", false, true, session
		);

		verify(produsRepository, times(1)).save(any(Produs.class));
	}

	@Test
	@DisplayName("3. Adăugare Ospătar ca Admin - Verificare flux și destinație URL")
	void testAdaugareOspatar_AdminAutentificat_RedirectFormularCuSucces() {
		session.setAttribute("isAdmin", true);

		String view = loginController.salveazaPersonalInBD(
				"ospatar@restaurant.com", "marius_osp", "parolaMarius1", session, redirectAttributes
		);

		assertEquals("redirect:/add-personal", view);

		Object flashMessage = redirectAttributes.getFlashAttributes().get("personalAdaugat");
		assertNotNull(flashMessage);
		assertEquals("Personal adăugat cu succes.", flashMessage);
	}

	@Test
	@DisplayName("4. Verificare adăugare Ospătar - Datele din model transmise corect în Repository")
	void testVerificareAdaugareOspatar_UtilizatorSalvatAreRolulOspatar() {
		session.setAttribute("isAdmin", true);

		loginController.salveazaPersonalInBD(
				"test@email.com", "ionut_test", "123456", session, redirectAttributes
		);

		verify(utilizatorRepository, times(1)).save(argThat(utilizator ->
				"test@email.com".equals(utilizator.getEmail()) &&
						"ionut_test".equals(utilizator.getUsername()) &&
						"123456".equals(utilizator.getPassword()) &&
						"OSPATAR".equals(utilizator.getRole())
		));
	}

	@Test
	@DisplayName("5. Verificarea timpului estimativ de la comenzi")
	void testSeteazaTimpEstimatComanda_ModificaSiSalveazaTimpul() {
		session.setAttribute("loggedInUser", "AndreiOspatar");
		session.setAttribute("isOspatar", true);

		Comanda comandaMacheta = new Comanda();
		comandaMacheta.setId(12L);
		comandaMacheta.setTimpEstimare(0);

		when(comandaVizualizareRepository.findById(12L)).thenReturn(Optional.of(comandaMacheta));

		String view = comenziPersonalController.seteazaTimpEstimat(12L, 35, session);

		assertEquals("redirect:/comenzi", view);
		assertEquals(35, comandaMacheta.getTimpEstimare());
		verify(comandaVizualizareRepository, times(1)).save(comandaMacheta);
	}
}