package com.paymybuddy.paymybuddy.exception;

public class BuddyNotFoundException extends RuntimeException{

    String buddyEmail;

    public BuddyNotFoundException(String message, String buddyEmail){
        super(message);
        this.buddyEmail = buddyEmail;
    }
}
