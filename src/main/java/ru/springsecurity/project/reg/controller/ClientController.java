package ru.springsecurity.project.reg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.springsecurity.project.reg.exception.ClientNotFoundException;
import ru.springsecurity.project.reg.exception.NoSuchRoleException;
import ru.springsecurity.project.reg.model.Client;
import ru.springsecurity.project.reg.service.ClientService;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping(value = "/manage/clients")
    public ResponseEntity<List<Client>> readListOfClients() {
        List<Client> clients = clientService.readAll();
        return clients != null && !clients.isEmpty()
                ? new ResponseEntity<>(clients, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/manage/client/{id}")
    public ResponseEntity<Client> readOneClient(@PathVariable(name = "id") long id) {
        try {
            return new ResponseEntity<>(clientService.read(id), HttpStatus.OK);
        } catch (ClientNotFoundException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping(value = "/add")
    public ResponseEntity<?> addClient(@RequestBody @Valid Client client) {
           if(clientService.create(client)) {
               return new ResponseEntity<>(HttpStatus.CREATED);
           }
           else {
               return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
           }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @DeleteMapping(value = "/manage/delete/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable(name = "id") long id) {
        final boolean deleted = clientService.delete(id);

        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @PatchMapping(value = "/manage/addrole/{role}/{id}")
    public ResponseEntity<?> addRoles(@PathVariable long id, @PathVariable String role)  {
        try {
            clientService.addRoleToClient(id, role);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (ClientNotFoundException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

        } catch (NoSuchRoleException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PatchMapping(value = "/manage/deleterole/{role}/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable long id, @PathVariable String role) {
        try {
            return clientService.deleteClientRole(id, role)
                    ? new ResponseEntity<>(HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

        } catch (ClientNotFoundException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

        } catch (NoSuchRoleException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}