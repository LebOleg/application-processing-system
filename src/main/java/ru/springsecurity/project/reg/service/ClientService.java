package ru.springsecurity.project.reg.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.springsecurity.project.reg.exception.ClientNotFoundException;
import ru.springsecurity.project.reg.exception.NoSuchRoleException;
import ru.springsecurity.project.reg.model.Client;
import java.util.List;


public interface ClientService extends UserDetailsService {

    boolean create(Client client);
    Client read(long id) throws ClientNotFoundException;
    List<Client> readAll();
    boolean delete(long id);
    boolean update(long id, Client client);
    boolean addRoleToClient(long id, String role) throws ClientNotFoundException, NoSuchRoleException;
    boolean deleteClientRole(long id, String s) throws ClientNotFoundException, NoSuchRoleException;
}
