package com.allweather.service;

import com.allweather.model.Contact;
import com.allweather.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;

    /**
     * Save a new contact form submission.
     */
    public Contact createContact(Contact contact) {
        contact.setIsRead(false);
        Contact saved = contactRepository.save(contact);
        log.info("New contact message received: ID={}, Name={}, Phone={}",
                saved.getId(), saved.getName(), saved.getPhone());
        return saved;
    }

    /**
     * Get all contact messages ordered newest first.
     */
    @Transactional(readOnly = true)
    public List<Contact> getAllContacts() {
        return contactRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get a single contact message by ID.
     */
    @Transactional(readOnly = true)
    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    /**
     * Get all unread contact messages.
     */
    @Transactional(readOnly = true)
    public List<Contact> getUnreadContacts() {
        return contactRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    /**
     * Mark a contact message as read.
     */
    public Optional<Contact> markAsRead(Long id) {
        return contactRepository.findById(id).map(contact -> {
            contact.setIsRead(true);
            return contactRepository.save(contact);
        });
    }
}
