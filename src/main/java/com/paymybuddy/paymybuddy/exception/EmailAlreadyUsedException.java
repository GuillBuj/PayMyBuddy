package com.paymybuddy.paymybuddy.exception;

public class EmailAlreadyUsedException extends RuntimeException{

    public EmailAlreadyUsedException(String message){
        super(message);
    }
}
