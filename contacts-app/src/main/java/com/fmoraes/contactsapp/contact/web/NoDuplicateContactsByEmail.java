package com.fmoraes.contactsapp.contact.web;

import com.fmoraes.contactsapp.contact.service.ContactService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoDuplicateContactsByEmailValidator.class)
public @interface NoDuplicateContactsByEmail {
    String message() default "There is already a contact with the same email address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

@ApplicationScoped
class NoDuplicateContactsByEmailValidator implements ConstraintValidator<NoDuplicateContactsByEmail, ContactFormData> {

    private final ContactService contactService;

    NoDuplicateContactsByEmailValidator(final ContactService contactService) {
        this.contactService = contactService;
    }

    @Override
    public boolean isValid(final ContactFormData formData, final ConstraintValidatorContext context) {
        return !contactService.contactWithEmailExists(formData.getEmail());
    }
}
