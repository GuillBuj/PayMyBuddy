package com.paymybuddy.paymybuddy.exception;

public class SelfSendException extends RuntimeException{
    
    public SelfSendException(String message){
        super(message);
    }
}
