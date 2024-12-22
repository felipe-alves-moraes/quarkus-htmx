package com.fmoraes.contactsapp.contact.web;

import com.fmoraes.contactsapp.contact.Contact;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;

@NoDuplicateContactsByEmail
public class ContactFormData {

    private long id;

    @NotBlank
    @FormParam("givenName")
    private String givenName;
    @NotBlank
    @FormParam("familyName")
    private String familyName;
    @NotBlank
    @FormParam("phone")
    private String phone;
    @Email
    @FormParam("email")
    private String email;

    public static ContactFormData from(final Contact contact) {
        final var formData = new ContactFormData();
        formData.setId(contact.getId().value());
        formData.setGivenName(contact.getGivenName());
        formData.setFamilyName(contact.getFamilyName());
        formData.setPhone(contact.getPhone());
        formData.setEmail(contact.getEmail());
        return formData;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
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
}
