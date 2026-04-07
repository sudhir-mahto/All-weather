package com.allweather.controller;

import com.allweather.model.Contact;
import com.allweather.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for contact form submissions.
 */
@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Slf4j
public class ContactController {

    private final ContactService contactService;

    /**
     * POST /api/contacts
     * Submit a contact/inquiry message.
     * Called by the frontend contact form.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> submitContact(@Valid @RequestBody Contact contact) {
        Contact saved = contactService.createContact(contact);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "success", true,
            "message", "Thank you " + saved.getName() + "! We received your message and will call you back soon.",
            "id", saved.getId()
        ));
    }

    /**
     * GET /api/contacts
     * Get all contact messages. Admin only.
     * Optional ?unread=true to filter unread messages.
     */
    @GetMapping
    public ResponseEntity<List<Contact>> getAllContacts(
            @RequestParam(required = false, defaultValue = "false") boolean unread) {
        if (unread) {
            return ResponseEntity.ok(contactService.getUnreadContacts());
        }
        return ResponseEntity.ok(contactService.getAllContacts());
    }

    /**
     * GET /api/contacts/{id}
     * Get a single contact message by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getContactById(@PathVariable Long id) {
        return contactService.getContactById(id)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Contact not found with id: " + id
            )));
    }

    /**
     * PUT /api/contacts/{id}/read
     * Mark a contact message as read.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        return contactService.markAsRead(id)
            .<ResponseEntity<?>>map(contact -> ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Message marked as read"
            )))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Contact not found with id: " + id
            )));
    }
}
