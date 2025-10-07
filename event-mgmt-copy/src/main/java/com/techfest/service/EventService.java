package com.techfest.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techfest.model.Event;
import com.techfest.repository.EventRepository;

@Service
@Transactional
public class EventService {
    
    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAll() {
        return eventRepository.findByOrderByEventDateAsc();
    }

    public void add(Event event) {
        eventRepository.save(event);
    }

    public void update(Event event) {
        eventRepository.save(event);
    }

    public void delete(int id) {
        eventRepository.deleteById(id);
    }

    public Event get(int id) {
        Optional<Event> event = eventRepository.findById(id);
        return event.orElse(null);
    }

    public int countRegistrations(int eventId) {
        return eventRepository.countRegistrationsByEventId(eventId);
    }
    
    public List<Event> searchByName(String eventName) {
        return eventRepository.findByEventNameContainingIgnoreCase(eventName);
    }
    
    public List<Event> getUpcomingEvents() {
        return eventRepository.findByEventDateAfter(LocalDate.now());
    }
    
    public List<Event> getEventsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return eventRepository.findEventsBetweenDates(startDate, endDate);
    }
}
