package com.paymybuddy.paymybuddy.exception;

public class UsernameAlreadyTakenException extends RuntimeException{

    public UsernameAlreadyTakenException(String message){
        super(message);
    }
}
