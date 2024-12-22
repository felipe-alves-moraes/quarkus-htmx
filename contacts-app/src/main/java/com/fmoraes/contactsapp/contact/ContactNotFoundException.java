package com.fmoraes.contactsapp.contact;

public class ContactNotFoundException extends RuntimeException {

    public ContactNotFoundException(final ContactId id) {
        super("Could not find contact with id " + id);
    }
}
