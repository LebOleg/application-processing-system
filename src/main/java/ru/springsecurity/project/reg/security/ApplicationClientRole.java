package ru.springsecurity.project.reg.security;

import org.springframework.security.core.GrantedAuthority;

public enum ApplicationClientRole implements GrantedAuthority {

    ADMIN,
    USER,
    OPERATOR;

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}