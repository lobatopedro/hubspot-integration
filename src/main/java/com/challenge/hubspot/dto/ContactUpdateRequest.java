package com.challenge.hubspot.dto;

public record ContactUpdateRequest(
        String firstName,
        String lastName,
        String email
) {}

