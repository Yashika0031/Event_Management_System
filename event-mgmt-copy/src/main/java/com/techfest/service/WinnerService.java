package com.techfest.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techfest.model.Event;
import com.techfest.model.Registration;
import com.techfest.model.Winner;
import com.techfest.repository.EventRepository;
import com.techfest.repository.RegistrationRepository;
import com.techfest.repository.WinnerRepository;

@Service
@Transactional
public class WinnerService {
    
    private final WinnerRepository winnerRepository;
    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;

    @Autowired
    public WinnerService(WinnerRepository winnerRepository, 
                        RegistrationRepository registrationRepository,
                        EventRepository eventRepository) {
        this.winnerRepository = winnerRepository;
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
    }

    public void setTop3(int eventId, String roll1, String roll2, String roll3) {
        Set<String> validRolls = registrationRepository.findByEventEventId(eventId)
                .stream().map(Registration::getRollNo).collect(Collectors.toSet());
                
        if (!validRolls.contains(roll1) || !validRolls.contains(roll2) || !validRolls.contains(roll3)) {
            throw new IllegalArgumentException("All winners must be registered participants of the event");
        }

        upsert(eventId, 1, roll1);
        upsert(eventId, 2, roll2);
        upsert(eventId, 3, roll3);
    }

    private void upsert(int eventId, int position, String rollNo) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new IllegalArgumentException("Event not found");
        }
        
        Event event = eventOpt.get();
        
        // Check if winner already exists for this position
        Optional<Winner> existingWinner = winnerRepository.findByEventIdAndPosition(eventId, position);
        
        Winner winner;
        if (existingWinner.isPresent()) {
            winner = existingWinner.get();
            winner.setRollNo(rollNo);
        } else {
            winner = new Winner();
            winner.setEvent(event);
            winner.setPosition(position);
            winner.setRollNo(rollNo);
        }
        
        winnerRepository.save(winner);
    }

    public List<Winner> getByEvent(int eventId) {
        return winnerRepository.findByEventEventIdOrderByPosition(eventId);
    }
    
    public List<Winner> getAll() {
        return winnerRepository.findAllOrderByEventDateAndPosition();
    }
    
    public Optional<Winner> getById(int id) {
        return winnerRepository.findById(id);
    }
    
    public Optional<Winner> getByRollNo(String rollNo) {
        return winnerRepository.findByRollNo(rollNo);
    }
    
    public void deleteWinner(int winnerId) {
        winnerRepository.deleteById(winnerId);
    }
}
