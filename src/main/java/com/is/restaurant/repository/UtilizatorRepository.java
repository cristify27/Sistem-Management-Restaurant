package com.is.restaurant.repository;

import com.is.restaurant.model.Utilizator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilizatorRepository extends JpaRepository<Utilizator, Long> {
    Optional<Utilizator> findByUsernameAndPassword(String username, String password);
}

