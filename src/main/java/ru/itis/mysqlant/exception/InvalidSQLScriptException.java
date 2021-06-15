package ru.itis.mysqlant.exception;

public class InvalidSQLScriptException extends RuntimeException {

    public InvalidSQLScriptException(String message) {
        super(message);
    }
}
