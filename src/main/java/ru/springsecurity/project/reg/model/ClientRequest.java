package ru.springsecurity.project.reg.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Calendar;

@Entity
@Table(name = "request")
public class ClientRequest {

    @Id
    @SequenceGenerator(name = "mySeqGenReq", sequenceName = "mySeqReq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(generator = "mySeqGenReq")
    private Long id;
    @NotNull
    private String text;
    @Enumerated(value = EnumType.STRING)
    private StateOfRequest state;
    private Calendar calendar;
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Client owner;

    public enum StateOfRequest {
        DRAFT,
        SENT,
        ACCEPTED,
        REFUSED
    }

    public ClientRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public StateOfRequest getState() {
        return state;
    }

    public void setState(StateOfRequest state) {
        this.state = state;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public Client getOwner() {
        return owner;
    }

    public void setOwner(Client owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "ClientRequest{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", state=" + state +
                ", owner=" + owner +
                '}';
    }
}