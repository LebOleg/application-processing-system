package ru.springsecurity.project.reg.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.springsecurity.project.reg.exception.ClientRequestNotFoundException;
import ru.springsecurity.project.reg.model.Client;
import ru.springsecurity.project.reg.model.ClientRequest;
import ru.springsecurity.project.reg.repository.ClientRepository;
import ru.springsecurity.project.reg.repository.ClientRequestRepository;
import ru.springsecurity.project.reg.service.ClientRequestService;
import java.util.Calendar;
import java.util.List;

@Service
public class RequestServiceImpl implements ClientRequestService {

    private final ClientRequestRepository clientRequestRepository;
    private final ClientRepository clientRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public RequestServiceImpl(ClientRequestRepository clientRequestRepository, ClientRepository clientRepository, ObjectMapper objectMapper) {
        this.clientRequestRepository = clientRequestRepository;
        this.clientRepository = clientRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean createReq(ClientRequest req, String username, String state) {
        req.setCalendar(Calendar.getInstance());
        Client client = clientRepository.findByUsername(username);
        req.setOwner(client);
        req.setState(ClientRequest.StateOfRequest.valueOf(state));
        clientRequestRepository.save(req);
        return true;
    }

    @Override
    public List<ClientRequest> getListOfUserCreatedReq(String username) {
        Client client = clientRepository.findByUsername(username);
        return clientRequestRepository.getRequestsByUserId(client.getId());
    }

    ClientRequest getUserCreatedReqById(long id, String username) throws ClientRequestNotFoundException {
        Client client = clientRepository.findByUsername(username);
        ClientRequest clientRequest = client.getRequests().stream()
                .filter(r -> r.getId() == id)
                .findFirst().orElseThrow(() -> new ClientRequestNotFoundException("This request doesn't exist"));
        return clientRequest;
    }

    public ClientRequest getSentReq(long id) throws ClientRequestNotFoundException {
        return clientRequestRepository.getSentRequestById(id)
                .orElseThrow(() -> new ClientRequestNotFoundException("Request doesn't exist"));
    }

    @Override
    public boolean editStateReqToSent(long id, String username) throws ClientRequestNotFoundException {
        ClientRequest clientRequest = getUserCreatedReqById(id, username);

        if(clientRequest.getState() == ClientRequest.StateOfRequest.DRAFT) {
            clientRequest.setState(ClientRequest.StateOfRequest.SENT);
            clientRequest.setCalendar(Calendar.getInstance());
        } else {
            throw new ClientRequestNotFoundException("This request is not a draft");
        }

        clientRequestRepository.save(clientRequest);
        return true;

    }

    @Override
    public boolean editTextOfReq(long id, String username, JsonPatch jsonPatch) throws ClientRequestNotFoundException {
        ClientRequest clientRequest = getUserCreatedReqById(id, username);

        if(clientRequest.getState() == ClientRequest.StateOfRequest.DRAFT) {
            try {
                ClientRequest clientRequestPatched = applyPatchToUserRequest(jsonPatch, clientRequest);
                clientRequestRepository.save(clientRequestPatched);
            } catch (JsonPatchException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            throw new ClientRequestNotFoundException("This request is not a draft");
        }
    }

    @Override
    public List<ClientRequest> getAllSentRequests() {
        return clientRequestRepository.getSentRequests();
    }

    @Override
     public boolean RefusedOrAcceptedReq(long id, String value) throws ClientRequestNotFoundException {
        ClientRequest clientRequest = getSentReq(id);
        clientRequest.setState(ClientRequest.StateOfRequest.valueOf(value));
        clientRequestRepository.save(clientRequest);
        return true;
    }

    @Override
    public boolean deleteDraftReq(long id, String username) throws ClientRequestNotFoundException {
        ClientRequest clientRequest = getUserCreatedReqById(id, username);
        if(clientRequest.getState() == ClientRequest.StateOfRequest.DRAFT) {
            clientRequestRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    private ClientRequest applyPatchToUserRequest(
            JsonPatch patch, ClientRequest targetClientRequest) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(objectMapper.convertValue(targetClientRequest, JsonNode.class));
        return objectMapper.treeToValue(patched, ClientRequest.class);
    }
}