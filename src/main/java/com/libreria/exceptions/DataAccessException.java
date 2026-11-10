package com.libreria.exceptions;

public class DataAccessException extends RuntimeException{
    // Constructor that accepts a message
    public DataAccessException(String message) {
        super(message);
    }
	
    private static final long serialVersionUID = 1L;

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
