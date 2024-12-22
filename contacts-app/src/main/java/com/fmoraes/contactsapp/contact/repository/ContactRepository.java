package com.fmoraes.contactsapp.contact.repository;

import com.fmoraes.contactsapp.contact.Contact;
import com.fmoraes.contactsapp.contact.ContactId;
import java.util.List;
import java.util.Optional;

public interface ContactRepository {
    ContactId nextId();
    List<Contact> findAll();
    Optional<Contact> findById(ContactId id);
    void save(Contact contact);
    Page<Contact> findAllWithNameContaining(String query, int page, int size);
    void deleteById(ContactId contactId);
    boolean existsByEmail(String email);
    Page<Contact> findAllOrderedByName(int page, int size);
}
