package com.fmoraes.contactsapp.contact.repository;

import com.fmoraes.contactsapp.contact.Contact;
import com.fmoraes.contactsapp.contact.ContactId;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import net.datafaker.Faker;

@ApplicationScoped
public class InMemoryContactRepository implements ContactRepository {

    private final AtomicLong sequence = new AtomicLong();
    private final Map<ContactId, Contact> values = new HashMap<>();

    public InMemoryContactRepository() {
        final var faker = new Faker();
        for (int i = 0; i < 100; i++) {
            final var name = faker.name();
            final var firstName = name.firstName();
            final var lastName = name.lastName();
            save(new Contact(nextId(), firstName, lastName, faker.phoneNumber().phoneNumber(),
                faker.internet().emailAddress(firstName.toLowerCase() + "." + lastName.toLowerCase())));
        }
    }

    @Override
    public ContactId nextId() {
        return new ContactId(sequence.incrementAndGet());
    }

    @Override
    public List<Contact> findAll() {
        return List.copyOf(values.values());
    }

    @Override
    public Optional<Contact> findById(final ContactId id) {
        return Optional.ofNullable(values.get(id));
    }

    @Override
    public void save(final Contact contact) {
        values.put(contact.getId(), contact);
    }

    @Override
    public Page<Contact> findAllWithNameContaining(final String query, int page, int size) {
        final var contactsUnpaged = values.values()
            .stream()
            .filter(it -> it.hasName(query))
            .toList();

        final var contacts = contactsUnpaged.stream()
            .sorted(Comparator.comparing(it -> it.getGivenName() + " " + it.getFamilyName()))
            .skip((long) page * size)
            .limit(size)
            .toList();
        return new Page<>(contacts, page, size, contactsUnpaged.size());
    }

    @Override
    public void deleteById(final ContactId contactId) {
        values.remove(contactId);
    }

    @Override
    public boolean existsByEmail(final String email) {
        return values.values()
            .stream()
            .anyMatch(it -> it.getEmail().equals(email));
    }

    @Override
    public Page<Contact> findAllOrderedByName(final int page, final int size) {
        final var contacts = values.values().stream()
            .sorted(Comparator.comparing(it -> it.getGivenName() + " " + it.getFamilyName()))
            .skip((long) page * size)
            .limit(size)
            .toList();
        return new Page<>(contacts, page, size, values.size());
    }
}
