package com.paymybuddy.paymybuddy.exception;

public class BuddyAlreadyAddedException extends RuntimeException{

    String buddyEmail;
    
    public BuddyAlreadyAddedException(String message, String buddyEmail){
        super(message);
        this.buddyEmail = buddyEmail;
    }
}
