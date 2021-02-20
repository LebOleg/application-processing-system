package ru.springsecurity.project.reg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.springsecurity.project.reg.model.ClientRequest;

import java.util.List;
import java.util.Optional;

public interface ClientRequestRepository extends JpaRepository<ClientRequest, Long> {
    @Query(value = "SELECT * FROM request r WHERE r.owner_id =:id",
    nativeQuery = true)
    List<ClientRequest> getRequestsByUserId(long id);
    @Query(value = "SELECT * FROM request r WHERE r.state = 'SENT'", nativeQuery = true)
    List<ClientRequest> getSentRequests();
    @Query(value = "SELECT * FROM request r WHERE r.state = 'SENT' AND r.id =:id", nativeQuery = true)
    Optional<ClientRequest> getSentRequestById(long id);
}