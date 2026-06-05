package com.ekart.inventory.exception;

public class OutOfStockException extends Exception{

    public OutOfStockException(String msg) {
        super(msg);
    }
}
