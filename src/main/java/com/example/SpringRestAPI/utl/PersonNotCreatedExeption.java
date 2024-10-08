package com.example.SpringRestAPI.utl;

public class PersonNotCreatedExeption extends RuntimeException {
    public PersonNotCreatedExeption(String message) {
        super(message);
    }
}
