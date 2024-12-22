package com.fmoraes.contactsapp.contact.service;

import com.fmoraes.contactsapp.contact.Contact;
import com.fmoraes.contactsapp.contact.repository.ContactRepository;
import com.fmoraes.contactsapp.contact.repository.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@ApplicationScoped
public class Archiver {

    private final ExecutorService executorService;
    private final ContactRepository contactRepository;
    private final Map<ArchiveId, ArchiveProcessInfo> archives = new HashMap<>();

    public Archiver(final ContactRepository contactRepository) {
        this.executorService = Executors.newCachedThreadPool();
        this.contactRepository = contactRepository;
    }

    public ArchiveId startArchiving() {
        final var archiveId = new ArchiveId(UUID.randomUUID());
        archives.put(archiveId, new ArchiveProcessInfo());
        final var future = executorService.submit(() -> {
            try {
                final var builder = new StringBuilder();
                addCsvHeader(builder);
                Page<Contact> page;
                int currentPage = 0;
                do {
                    page = contactRepository.findAllOrderedByName(currentPage, 10);
                    for (var contact : page.values()) {
                        addCsvRow(builder, contact);
                    }
                    int elementsArchived = (page.number() + 1) * page.size();
                    int progress = (int) ((double) elementsArchived / page.totalElements() * 100);
                    archives.get(archiveId).setProgress(progress);
                    currentPage++;
                    Thread.sleep(200);
                } while ((page.number() + 1) * page.size() < page.totalElements());

                archives.get(archiveId).setStatus(ArchiveProcessInfo.Status.COMPLETE);

                return builder.toString();
            } catch (Exception e) {
                archives.get(archiveId).setStatus(ArchiveProcessInfo.Status.FAILED);
                return null;
            }
        });

        archives.get(archiveId).setFuture(future);
        return archiveId;
    }

    public ArchiveProcessInfo getArchiveProcessInfo(ArchiveId archiveId) {
        return archives.get(archiveId);
    }

    private static void addCsvHeader(StringBuilder builder) {
        builder.append("Given name, Family name, Email, Phone")
            .append(System.lineSeparator());
    }

    private static void addCsvRow(StringBuilder builder, Contact
        contact) {
        builder.append(contact.getGivenName())
            .append(",")
            .append(contact.getFamilyName())
            .append(",")
            .append(contact.getEmail())
            .append(",")
            .append(contact.getPhone())
            .append(System.lineSeparator());
    }
}
