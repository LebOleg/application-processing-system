package ru.springsecurity.project.reg.controller;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.springsecurity.project.reg.exception.ClientRequestNotFoundException;
import ru.springsecurity.project.reg.model.ClientRequest;
import ru.springsecurity.project.reg.service.ClientService;
import ru.springsecurity.project.reg.service.ClientRequestService;
import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RequestController {
    private ClientService clientService;
    private ClientRequestService clientRequestService;

    @Autowired
    public RequestController(ClientService clientService, ClientRequestService clientRequestService) {
        this.clientService = clientService;
        this.clientRequestService = clientRequestService;
    }

    @PostMapping(value = "/user/request/draft")
    public ResponseEntity<?> createAndSaveReq(@RequestBody @Valid ClientRequest clientRequest, Principal principal) {
        clientRequestService.createReq(clientRequest, principal.getName(), "DRAFT");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/user/createrequest/send")
    public ResponseEntity<?> createAndSendReq(@RequestBody @Valid ClientRequest clientRequest, Principal principal) {
        clientRequestService.createReq(clientRequest, principal.getName(), "SENT");
        return new ResponseEntity<>(HttpStatus.CREATED);
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

    @GetMapping(value = "/user/requests")
    public ResponseEntity<List<ClientRequest>> getClientsReq(Principal principal) {
        List<ClientRequest> requests = clientRequestService.getListOfUserCreatedReq(principal.getName());
        return requests != null
                ? new ResponseEntity<>(requests, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping(value = "/user/request/{id}/send")
    public ResponseEntity<?> sendReq(@PathVariable long id, Principal principal) {
        try {
            clientRequestService.editStateReqToSent(id, principal.getName());
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (ClientRequestNotFoundException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PatchMapping(value = "/user/request/{id}/text", consumes = "application/json-patch+json")
    public ResponseEntity<?> editDraftReq(@PathVariable long id, Principal principal, @RequestBody JsonPatch patch) {
        try {
            clientRequestService.editTextOfReq(id, principal.getName(), patch);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (ClientRequestNotFoundException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping(value = "/user/delete/request/{id}")
    public ResponseEntity<?> deleteUserDraftReq(@PathVariable long id, Principal principal) {
        try {
            return clientRequestService.deleteDraftReq(id, principal.getName())
                    ? new ResponseEntity<>(HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } catch (ClientRequestNotFoundException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping(value = "/operator/requests")
    public ResponseEntity<List<ClientRequest>> showUserSentReq() {
        List<ClientRequest> clientRequests = clientRequestService.getAllSentRequests();
        if(clientRequests == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(clientRequests, HttpStatus.OK);
        }
    }

    @PatchMapping(value = "/operator/request/{id}/accept")
    public ResponseEntity<?> acceptUserReq(@PathVariable long id) {
        try {
            clientRequestService.RefusedOrAcceptedReq(id, ClientRequest.StateOfRequest.ACCEPTED.toString());
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (ClientRequestNotFoundException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PatchMapping(value = "/operator/request/{id}/refuse")
    public ResponseEntity<?> refuseUserReq(@PathVariable long id) {
        try {
            clientRequestService.RefusedOrAcceptedReq(id, ClientRequest.StateOfRequest.REFUSED.toString());
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (ClientRequestNotFoundException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}