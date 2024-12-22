package com.fmoraes.contactsapp.contact.service;

import com.fmoraes.contactsapp.contact.Contact;
import com.fmoraes.contactsapp.contact.ContactId;
import com.fmoraes.contactsapp.contact.ContactNotFoundException;
import com.fmoraes.contactsapp.contact.repository.ContactRepository;
import com.fmoraes.contactsapp.contact.repository.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(final ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public List<Contact> getAll() {
        return contactRepository.findAll();
    }

    public Contact storeNewContact(String givenName, String familyName, String phone, String email) {
        final var contact = new Contact(contactRepository.nextId(), givenName, familyName, phone, email);
        contactRepository.save(contact);
        return contact;
    }

    public Page<Contact> searchContacts(final String query, int page) {
        return contactRepository.findAllWithNameContaining(query, page, 10);
    }

    public Contact getContact(final ContactId id) {
        return contactRepository.findById(id)
            .orElseThrow(() -> new ContactNotFoundException(id));
    }

    public void updateContact(final ContactId contactId, final String givenName, final String familyName,
        final String phone, final String email) {
        final var contact = getContact(contactId);
        contact.setGivenName(givenName);
        contact.setFamilyName(familyName);
        contact.setPhone(phone);
        contact.setEmail(email);

        contactRepository.save(contact);
    }

    public void deleteContact(final ContactId contactId) {
        contactRepository.deleteById(contactId);
    }

    public boolean contactWithEmailExists(final String email) {
        return contactRepository.existsByEmail(email);
    }

    public Page<Contact> getAll(int page) {
        return contactRepository.findAllOrderedByName(page, 10);
    }
}
