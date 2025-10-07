package com.techfest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techfest.model.Event;
import com.techfest.model.Registration;
import com.techfest.repository.EventRepository;
import com.techfest.repository.RegistrationRepository;
import com.techfest.validation.RegistrationValidator;

@Service
@Transactional
public class RegistrationService {
    
    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final RegistrationValidator validator;

    @Autowired
    public RegistrationService(RegistrationRepository registrationRepository, 
                              EventRepository eventRepository,
                              RegistrationValidator validator) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.validator = validator;
    }

    public void removeParticipant(int registrationId) {
        registrationRepository.deleteById(registrationId);
    }

    public Registration register(Registration registration) {
        List<String> errors = validator.validate(registration);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }

        // Check event exists and capacity
        Optional<Event> eventOpt = eventRepository.findById(registration.getEventId());
        if (eventOpt.isEmpty()) {
            throw new IllegalArgumentException("Event not found");
        }
        
        Event event = eventOpt.get();
        registration.setEvent(event);
        
        int currentRegistrations = registrationRepository.countByEventId(registration.getEventId());
        if (currentRegistrations >= event.getMaxParticipants()) {
            throw new IllegalStateException("Max participants reached");
        }

        if (registrationRepository.existsByRollNoAndEventEventId(registration.getRollNo(), registration.getEventId())) {
            throw new IllegalStateException("Duplicate: Roll already registered for this event");
        }

        return registrationRepository.save(registration);
    }

    public List<Registration> getByEvent(int eventId) {
        return registrationRepository.findByEventIdOrderByName(eventId);
    }
    
    public List<Registration> getAll() {
        return registrationRepository.findAll();
    }
    
    public Optional<Registration> getById(int id) {
        return registrationRepository.findById(id);
    }
    
    public Optional<Registration> getByRollNo(String rollNo) {
        return registrationRepository.findByRollNo(rollNo);
    }
    
    public List<Registration> searchByName(String name) {
        return registrationRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<Registration> getByDepartment(String dept) {
        return registrationRepository.findByDeptIgnoreCase(dept);
    }
}
