package com.mycompany.myapp.exception;

public class CustomerNotFoundException extends ResourceNotFoundException {

    public CustomerNotFoundException(Long id) {
        super("Customer with Id '%d' not found".formatted(id));
    }
}
