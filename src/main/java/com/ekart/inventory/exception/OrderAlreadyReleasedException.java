package com.ekart.inventory.exception;

public class OrderAlreadyReleasedException extends Exception{

    public OrderAlreadyReleasedException(String msg) {
        super(msg);
    }
}
