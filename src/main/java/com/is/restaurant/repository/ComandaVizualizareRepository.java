package com.is.restaurant.repository;

import com.is.restaurant.model.Comanda;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComandaVizualizareRepository extends JpaRepository<Comanda, Long> {

    @EntityGraph(attributePaths = "produse")
    List<Comanda> findAllByOrderByIdDesc();
}