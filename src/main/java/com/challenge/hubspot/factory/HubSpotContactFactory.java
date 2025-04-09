package com.challenge.hubspot.factory;

import java.util.Map;

public class HubSpotContactFactory {

    private HubSpotContactFactory() {}

    public static Map<String, Object> createBasicContact(String email, String firstName, String lastName) {
        return Map.of(
                "email", email,
                "firstname", firstName,
                "lastname", lastName
        );
    }

    public static Map<String, Object> createFullContact(String email, String firstName, String lastName, String phone, String company) {
        return Map.of(
                "email", email,
                "firstname", firstName,
                "lastname", lastName,
                "phone", phone,
                "company", company
        );
    }
}

