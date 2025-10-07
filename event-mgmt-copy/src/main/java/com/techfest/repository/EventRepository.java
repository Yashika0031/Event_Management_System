package com.techfest.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techfest.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    
    List<Event> findByOrderByEventDateAsc();
    
    List<Event> findByEventNameContainingIgnoreCase(String eventName);
    
    List<Event> findByEventDateAfter(LocalDate date);
    
    @Query("SELECT e FROM Event e WHERE e.eventDate >= :startDate AND e.eventDate <= :endDate")
    List<Event> findEventsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.event.eventId = :eventId")
    int countRegistrationsByEventId(@Param("eventId") int eventId);
}