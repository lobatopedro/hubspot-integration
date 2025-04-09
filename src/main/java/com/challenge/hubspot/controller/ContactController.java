package com.challenge.hubspot.controller;

import com.challenge.hubspot.dto.ContactDTO;
import com.challenge.hubspot.service.ContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}


