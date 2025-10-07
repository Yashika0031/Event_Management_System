package com.techfest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techfest.model.Event;
import com.techfest.service.EventService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String listEvents(Model model) {
        List<Event> events = eventService.getAll();
        model.addAttribute("events", events);
        return "events/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        return "events/form";
    }

    @PostMapping
    public String createEvent(@Valid @ModelAttribute Event event, 
                             BindingResult result, 
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "events/form";
        }
        
        try {
            eventService.add(event);
            redirectAttributes.addFlashAttribute("successMessage", "Event created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating event: " + e.getMessage());
        }
        
        return "redirect:/events";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable int id, Model model) {
        Event event = eventService.get(id);
        if (event == null) {
            return "redirect:/events";
        }
        model.addAttribute("event", event);
        return "events/form";
    }

    @PostMapping("/{id}")
    public String updateEvent(@PathVariable int id, 
                             @Valid @ModelAttribute Event event, 
                             BindingResult result, 
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "events/form";
        }
        
        try {
            event.setEventId(id);
            eventService.update(event);
            redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating event: " + e.getMessage());
        }
        
        return "redirect:/events";
    }

    @GetMapping("/{id}/delete")
    public String deleteEvent(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            eventService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting event: " + e.getMessage());
        }
        
        return "redirect:/events";
    }

    @GetMapping("/{id}")
    public String viewEvent(@PathVariable int id, Model model) {
        Event event = eventService.get(id);
        if (event == null) {
            return "redirect:/events";
        }
        
        int registrationCount = eventService.countRegistrations(id);
        model.addAttribute("event", event);
        model.addAttribute("registrationCount", registrationCount);
        model.addAttribute("availableSlots", event.getMaxParticipants() - registrationCount);
        
        return "events/view";
    }
}