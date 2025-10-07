package com.techfest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techfest.model.Registration;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Integer> {
    
    List<Registration> findByEventEventId(int eventId);
    
    Optional<Registration> findByRollNo(String rollNo);
    
    boolean existsByRollNo(String rollNo);
    
    boolean existsByRollNoAndEventEventId(String rollNo, int eventId);
    
    List<Registration> findByNameContainingIgnoreCase(String name);
    
    List<Registration> findByDeptIgnoreCase(String dept);
    
    @Query("SELECT r FROM Registration r WHERE r.event.eventId = :eventId ORDER BY r.name")
    List<Registration> findByEventIdOrderByName(@Param("eventId") int eventId);
    
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.event.eventId = :eventId")
    int countByEventId(@Param("eventId") int eventId);
}