package com.fmoraes.contactsapp.contact.web;

import io.quarkiverse.renarde.htmx.HxController;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/")
public class HomeController extends HxController {

    @GET
    @Path("/")
    public void index() {
        redirect(ContactController.class).viewContacts(null, 0);
    }
}
