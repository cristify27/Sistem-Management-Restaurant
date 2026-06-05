package com.is.restaurant.repository;

import com.is.restaurant.model.Comanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComandaRepository extends JpaRepository<Comanda, Long> {

    List<Comanda> findByStatusNot(String status);
}