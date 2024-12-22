package com.fmoraes.contactsapp.contact.web;

import io.quarkus.qute.TemplateExtension;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@TemplateExtension
public class Extensions {

    static Integer multiply(Integer first, Integer second) {
        return first * second;
    }
}