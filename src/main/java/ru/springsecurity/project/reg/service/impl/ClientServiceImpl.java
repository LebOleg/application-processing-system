package ru.springsecurity.project.reg.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.springsecurity.project.reg.exception.ClientNotFoundException;
import ru.springsecurity.project.reg.exception.NoSuchRoleException;
import ru.springsecurity.project.reg.model.Client;
import ru.springsecurity.project.reg.repository.ClientRepository;
import ru.springsecurity.project.reg.repository.ClientRequestRepository;
import ru.springsecurity.project.reg.security.ApplicationClientRole;
import ru.springsecurity.project.reg.service.ClientService;
import java.util.Collections;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientRequestRepository clientRequestRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, ClientRequestRepository clientRequestRepository, PasswordEncoder bCryptPasswordEncoder, ObjectMapper objectMapper) {
        this.clientRepository = clientRepository;
        this.clientRequestRepository = clientRequestRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public boolean create(Client client) {
        Client cl = clientRepository.findByUsername(client.getUsername());

        if(cl != null) {
            return false;
        } else {
            Collections.addAll(client.getRoles(), ApplicationClientRole.USER);
            client.setPassword(bCryptPasswordEncoder.encode(client.getPassword()));
            clientRepository.save(client);
            return true;
        }
    }

    @Override
    public Client read(long id) throws ClientNotFoundException {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client Not Found"));

        return client;
    }

    @Override
    public List<Client> readAll() {
        return clientRepository.findAll();
    }

    @Override
    public boolean delete(long id) {
       if(clientRepository.existsById(id)) {
           clientRepository.deleteById(id);
           return true;
       } else {
           return false;
       }
    }

    @Override
    public boolean update(long id, Client client) {
        if(clientRepository.existsById(id)) {
            client.setId(id);
            client.setPassword(bCryptPasswordEncoder.encode(client.getPassword()));
            clientRepository.save(client);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Client client = clientRepository.findByUsername(s);

        if(client == null) {
            throw new UsernameNotFoundException("User not found");
        } else {
            return client;
        }
    }

    @Override
    public boolean addRoleToClient(long id, String role ) throws ClientNotFoundException, NoSuchRoleException {
        Client client = read(id);

        try {
            ApplicationClientRole roleToInsert = ApplicationClientRole.valueOf(role.toUpperCase());
            client.getRoles().add(roleToInsert);
            clientRepository.save(client);
        } catch (IllegalArgumentException e) {
            throw new NoSuchRoleException("Role " + role.toUpperCase() + " doesn't exist");
        }

        return true;
    }

    @Override
    public boolean deleteClientRole(long id, String role) throws ClientNotFoundException, NoSuchRoleException {
        Client client = read(id);

        try {
            boolean res = client.getRoles().remove(ApplicationClientRole.valueOf(role.toUpperCase()));
            if (res) {
                clientRepository.save(client);
                return true;
            } else {
                return false;
            }

        } catch (IllegalArgumentException e) {
            throw new NoSuchRoleException("Role " + role.toUpperCase() + " doesn't exist");
        }
    }
}