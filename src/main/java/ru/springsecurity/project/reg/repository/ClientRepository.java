package ru.springsecurity.project.reg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.springsecurity.project.reg.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByUsername(String username);
}