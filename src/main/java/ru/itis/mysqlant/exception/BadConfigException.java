package ru.itis.mysqlant.exception;

public class BadConfigException extends RuntimeException {

    public BadConfigException(String message) {
        super(message);
    }
}
