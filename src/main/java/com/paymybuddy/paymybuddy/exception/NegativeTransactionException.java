package com.paymybuddy.paymybuddy.exception;

public class NegativeTransactionException  extends RuntimeException{
    
    public NegativeTransactionException(String message){
        super(message);
    }
}
