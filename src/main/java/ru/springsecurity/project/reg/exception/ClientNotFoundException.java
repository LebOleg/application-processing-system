package ru.springsecurity.project.reg.exception;

public class ClientNotFoundException extends Exception {
    public ClientNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
