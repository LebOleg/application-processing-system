package ru.springsecurity.project.reg.service;

import com.github.fge.jsonpatch.JsonPatch;
import ru.springsecurity.project.reg.exception.ClientRequestNotFoundException;
import ru.springsecurity.project.reg.model.ClientRequest;

import java.util.List;

public interface ClientRequestService {
    boolean createReq(ClientRequest req, String username, String state);
    List<ClientRequest> getListOfUserCreatedReq(String username);
    ClientRequest getSentReq(long id) throws ClientRequestNotFoundException;
    boolean editStateReqToSent(long id, String username) throws ClientRequestNotFoundException;
    boolean deleteDraftReq(long id, String username) throws ClientRequestNotFoundException;
    boolean editTextOfReq(long id, String username, JsonPatch jsonPatch) throws ClientRequestNotFoundException;
    List<ClientRequest> getAllSentRequests();
    boolean RefusedOrAcceptedReq(long id, String value) throws ClientRequestNotFoundException;
}
