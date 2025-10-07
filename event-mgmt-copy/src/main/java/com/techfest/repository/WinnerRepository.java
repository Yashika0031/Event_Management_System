package com.techfest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techfest.model.Winner;

@Repository
public interface WinnerRepository extends JpaRepository<Winner, Integer> {
    
    List<Winner> findByEventEventId(int eventId);
    
    List<Winner> findByEventEventIdOrderByPosition(int eventId);
    
    Optional<Winner> findByRollNo(String rollNo);
    
    boolean existsByRollNoAndEventEventId(String rollNo, int eventId);
    
    boolean existsByEventEventIdAndPosition(int eventId, int position);
    
    @Query("SELECT w FROM Winner w WHERE w.event.eventId = :eventId AND w.position = :position")
    Optional<Winner> findByEventIdAndPosition(@Param("eventId") int eventId, @Param("position") int position);
    
    @Query("SELECT w FROM Winner w ORDER BY w.event.eventDate DESC, w.position ASC")
    List<Winner> findAllOrderByEventDateAndPosition();
}