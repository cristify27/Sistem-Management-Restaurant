package com.is.restaurant.repository;

import com.is.restaurant.model.Produs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdusRepository extends JpaRepository<Produs, Long> {

    List<Produs> findByCategorie(String categorie);

    List<Produs> findByCategorieIn(List<String> categorii);
}