package com.paymybuddy.paymybuddy.exception;

public class NotEnoughMoneyException extends RuntimeException{

    public NotEnoughMoneyException(String message){
        super(message);
    }
}
