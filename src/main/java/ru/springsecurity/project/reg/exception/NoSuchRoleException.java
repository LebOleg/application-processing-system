package ru.springsecurity.project.reg.exception;

public class NoSuchRoleException extends Exception {
    public NoSuchRoleException(String message) {
        super(message);
    }
}
