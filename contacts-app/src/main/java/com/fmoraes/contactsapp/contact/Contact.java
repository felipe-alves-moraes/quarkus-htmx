package com.fmoraes.contactsapp.contact;

import java.util.Locale;

public class Contact {
    private ContactId id;
    private String givenName;
    private String familyName;
    private String phone;
    private String email;

    public Contact(final ContactId id, final String givenName, final String familyName, final String phone,
        final String email) {
        this.id = id;
        this.givenName = givenName;
        this.familyName = familyName;
        this.phone = phone;
        this.email = email;
    }

    public ContactId getId() {
        return id;
    }

    public void setId(final ContactId id) {
        this.id = id;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(final String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(final String familyName) {
        this.familyName = familyName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public boolean hasName(final String name) {
        return givenName.toLowerCase(Locale.ROOT).contains(name) || familyName.toLowerCase(Locale.ROOT).contains(name);
    }
}
