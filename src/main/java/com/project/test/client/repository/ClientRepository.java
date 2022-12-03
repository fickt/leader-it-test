package com.project.test.client.repository;

import com.project.test.client.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findAll();

    Optional<Client> findById(Long clientId);
    boolean existsById(Long clientId);
}
