package com.techfest.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.techfest.model.Registration;

@Component
public class RegistrationValidator {
    private static final String ROLL_REGEX = "^[A-Za-z0-9-]{4,20}$";
    private static final String CONTACT_REGEX = "^[0-9+\\-() ]{7,20}$";

    public List<String> validate(Registration r) {
        List<String> errors = new ArrayList<>();
        if (isBlank(r.getName())) errors.add("Name is required");
        if (isBlank(r.getRollNo())) errors.add("Roll No is required");
        if (isBlank(r.getDept())) errors.add("Department is required");
        if (isBlank(r.getContact())) errors.add("Contact is required");

        if (!isBlank(r.getRollNo()) && !r.getRollNo().matches(ROLL_REGEX))
            errors.add("Invalid roll number format");
        if (!isBlank(r.getContact()) && !r.getContact().matches(CONTACT_REGEX))
            errors.add("Invalid contact format");
        if (r.getEventId() <= 0) errors.add("Event selection is required");

        return errors;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
