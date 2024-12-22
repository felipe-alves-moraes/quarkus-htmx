package com.fmoraes.contactsapp.contact.web;

import com.fmoraes.contactsapp.contact.Contact;
import com.fmoraes.contactsapp.contact.ContactId;
import com.fmoraes.contactsapp.contact.repository.Page;
import com.fmoraes.contactsapp.contact.service.ArchiveId;
import com.fmoraes.contactsapp.contact.service.ArchiveProcessInfo;
import com.fmoraes.contactsapp.contact.service.ArchiveProcessInfo.Status;
import com.fmoraes.contactsapp.contact.service.Archiver;
import com.fmoraes.contactsapp.contact.service.ContactService;
import io.quarkiverse.renarde.htmx.HxController;
import io.quarkiverse.renarde.router.Router;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.vertx.core.http.HttpServerResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.jboss.resteasy.reactive.server.multipart.MultipartFormDataOutput;

@ApplicationScoped
@Path("/contacts")
public class ContactController extends HxController {

    private final ContactService contactService;
    private final Archiver archiver;

    public ContactController(final ContactService contactService, final Archiver archiver) {
        this.contactService = contactService;
        this.archiver = archiver;
    }

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance list(List<Contact> contacts, String query, int page, int size, int totalElements);
        public static native TemplateInstance fragments$contactsTable(List<Contact> contacts, String query, int page, int size, int totalElements);
        public static native TemplateInstance edit(ContactFormData formData, EditMode editMode);
        public static native TemplateInstance view(final Contact contact);
        public static native TemplateInstance archive(final UUID archiveId, final Status status, final int progress);
    }

    @GET
    @Path("")
    public TemplateInstance viewContacts(@QueryParam("q") String query, @QueryParam("page") @DefaultValue("0") int page) {
        Page<Contact> contactsPage;
        if (query != null) {
            contactsPage = contactService.searchContacts(query, page);
        } else {
            contactsPage = contactService.getAll(page);
        }

        if (isHxRequest() && !"delete-button".equals(hx(HxRequestHeader.TRIGGER))) {
            return Templates.fragments$contactsTable(contactsPage.values(), query, contactsPage.number(), contactsPage.size(), contactsPage.totalElements());
        }
        return Templates.list(contactsPage.values(), query, contactsPage.number(), contactsPage.size(), contactsPage.totalElements());
    }

    @GET
    @Path("{id}")
    public TemplateInstance viewContact(@PathParam("id") long id) {
        final var contact = contactService.getContact(new ContactId(id));
        return Templates.view(contact);
    }


    @GET
    @Path("/new")
    public TemplateInstance newContact() {
        return Templates.edit(new ContactFormData(), EditMode.CREATE);
    }

    @POST
    @Path("/new")
    public TemplateInstance createNewContact(@Valid @BeanParam ContactFormData formData) {
        if (validationFailed()) {
            return Templates.edit(formData, EditMode.CREATE);
        }

        contactService.storeNewContact(formData.getGivenName(), formData.getFamilyName(), formData.getPhone(), formData.getEmail());

        return viewContacts(null, 0);
    }

    @POST
    @Path("/validate")
    public TemplateInstance validateNewContact(@Valid @BeanParam ContactFormData formData) {
        onlyHxRequest();
        validationFailed();

        return Templates.edit(formData, EditMode.CREATE);
    }

    @GET
    @Path("{id}/edit")
    public TemplateInstance editContact(@PathParam("id") long id) {
        final var contact = contactService.getContact(new ContactId(id));
        return Templates.edit(ContactFormData.from(contact), EditMode.UPDATE);
    }

    @POST
    @Path("{id}/edit")
    public TemplateInstance editContact(@PathParam("id") long id, @Valid @BeanParam ContactFormData formData) {
        if (validationFailed()) {
            return Templates.edit(formData, EditMode.UPDATE);
        }

        contactService.updateContact(new ContactId(id),
            formData.getGivenName(), formData.getFamilyName(), formData.getPhone(), formData.getEmail());

        return viewContacts(null, 0);
    }

    @DELETE
    @Path("{id}")
    public Response deleteContact(@PathParam("id") long id) {
        contactService.deleteContact(new ContactId(id));

        if ("delete-button".equals(hx(HxRequestHeader.TRIGGER))) {
            flash("successMessage", "Deleted Contact!");
            final var uri = Router.getAbsoluteURI(ContactController::viewContacts, null, 0);
            return Response.seeOther(uri).build();
        }

        return Response.ok("").build();
    }

    @GET
    @Path("/archives/{id}")
    public MultipartFormDataOutput downloadArchive(@PathParam("id") UUID id)
        throws ExecutionException, InterruptedException {
        final var archiveId = new ArchiveId(id);
        final var archiveProcessInfo = archiver.getArchiveProcessInfo(archiveId);
        final var archive = archiveProcessInfo.getFuture().get();

        MultipartFormDataOutput out = new MultipartFormDataOutput();
        out.addFormData("archive", archive, MediaType.MULTIPART_FORM_DATA_TYPE, "archive.csv")
            .getHeaders()
            .putSingle("Content-Disposition", "attachment; filename=archive.csv");

        return out;
    }

    @GET
    @Path("/archives/{id}/info")
    public TemplateInstance getArchive(@PathParam("id") UUID id) {
        onlyHxRequest();
        final var archiveId = new ArchiveId(id);
        final var archiveProcessInfo = archiver.getArchiveProcessInfo(archiveId);

        return Templates.archive(archiveId.value(), archiveProcessInfo.getStatus(), archiveProcessInfo.getProgress());
    }

    @POST
    @Path("/archives")
    public TemplateInstance createArchive() {
        onlyHxRequest();
        final var archiveId = archiver.startArchiving();
        final var archiveProcessInfo = archiver.getArchiveProcessInfo(archiveId);

        return Templates.archive(archiveId.value(), archiveProcessInfo.getStatus(), archiveProcessInfo.getProgress());
    }
}
