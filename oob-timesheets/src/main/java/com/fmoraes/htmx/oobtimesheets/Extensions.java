package com.fmoraes.htmx.oobtimesheets;

import io.quarkus.qute.TemplateExtension;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@TemplateExtension
public class Extensions {

    static String getKey(LocalDate day) {
        return "dayTotal_" + day.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
