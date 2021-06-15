package ru.itis.mysqlant.exception;

public class SQLScriptNotFoundException extends RuntimeException {

    public SQLScriptNotFoundException(String message) {
        super(message);
    }
}
