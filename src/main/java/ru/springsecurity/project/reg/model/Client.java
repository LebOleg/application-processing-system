package ru.springsecurity.project.reg.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.security.core.userdetails.UserDetails;
import ru.springsecurity.project.reg.security.ApplicationClientRole;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "clients")
public class Client implements UserDetails {

    @Id
    @SequenceGenerator(name = "mySeqGen", sequenceName = "mySeq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(generator = "mySeqGen")
    private Long id;
    private String name;
    private String email;
    @NotBlank(message = "Username may not be blank")
    private String username;
    @NotBlank(message = "Password may not to be blank")
    private String password;
    @ElementCollection(targetClass = ApplicationClientRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "clients_roles")
    @Enumerated(EnumType.STRING)
    private Set<ApplicationClientRole> roles = new HashSet<>();
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ClientRequest> requests;

    public Client() {
    }

    public Set<ApplicationClientRole> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<ApplicationClientRole> roles) {
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Set<ApplicationClientRole> getAuthorities() {
        return getRoles();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ClientRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<ClientRequest> requests) {
        this.requests = requests;
    }
}