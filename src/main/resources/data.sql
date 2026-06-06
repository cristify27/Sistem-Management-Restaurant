INSERT INTO produse (denumire, categorie, pret, ingrediente, este_picant, este_vegetarian, este_disponibil) VALUES
('Apă plată', 'Băuturi nespirtoase', 8.0, 'Apă', false, true, true),
('Apă minerală', 'Băuturi nespirtoase', 9.0, 'Apă mineralizată', false, true, true),
('Limonadă', 'Băuturi nespirtoase', 15.0, 'Lămâie, apă, zahăr, mentă', false, true, true),
('Bruschete cu roșii', 'Aperitive', 20.0, 'Pâine prăjită, roșii, usturoi, ulei de măsline', false, true, true),
('Pesto', 'Aperitive', 22.0, 'Pâine prăjită, busuioc, parmezan, muguri de pin, ulei de măsline', false, true, true),
('Bruschete cu somon', 'Aperitive', 30.0, 'Pâine prăjită, somon afumat, cremă de brânză', false, false, true);

INSERT INTO utilizatori (username, password, role) VALUES
('ADMIN', 'a123', 'ADMIN'),
('Mircea', '123', 'OSPATAR');
