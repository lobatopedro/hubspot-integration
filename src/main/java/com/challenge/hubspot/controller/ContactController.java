package com.challenge.hubspot.controller;

import com.challenge.hubspot.dto.ContactDTO;
import com.challenge.hubspot.dto.ContactUpdateRequest;
import com.challenge.hubspot.service.ContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/contacts")
@Slf4j
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public Mono<ResponseEntity<String>> createContact(@RequestBody ContactDTO contactDTO) {
        return contactService.createContact(
                contactDTO.getEmail(),
                contactDTO.getFirstName(),
                contactDTO.getLastName(),
                contactDTO.getPhone(),
                contactDTO.getCompany()
        );
    }

    @PutMapping("/{contactId}")
    public ResponseEntity<Mono<String>> updateContact(@PathVariable String contactId, @RequestBody ContactUpdateRequest request) {
        return ResponseEntity.ok(contactService.updateContact(contactId, request));
    }

}


