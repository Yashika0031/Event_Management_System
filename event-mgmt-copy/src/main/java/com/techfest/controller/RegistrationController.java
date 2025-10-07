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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techfest.model.Event;
import com.techfest.model.Registration;
import com.techfest.service.EventService;
import com.techfest.service.RegistrationService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final EventService eventService;

    @Autowired
    public RegistrationController(RegistrationService registrationService, EventService eventService) {
        this.registrationService = registrationService;
        this.eventService = eventService;
    }

    @GetMapping
    public String listRegistrations(@RequestParam(value = "eventId", required = false) Integer eventId, Model model) {
        List<Registration> registrations;
        
        if (eventId != null) {
            registrations = registrationService.getByEvent(eventId);
            Event event = eventService.get(eventId);
            model.addAttribute("event", event);
        } else {
            registrations = registrationService.getAll();
        }
        
        model.addAttribute("registrations", registrations);
        model.addAttribute("events", eventService.getAll());
        return "registrations/list";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registration", new Registration());
        model.addAttribute("events", eventService.getUpcomingEvents());
        return "registrations/register";
    }

    @PostMapping("/register")
    public String registerStudent(@Valid @ModelAttribute Registration registration, 
                                 BindingResult result, 
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("events", eventService.getUpcomingEvents());
            return "registrations/register";
        }
        
        try {
            registrationService.register(registration);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Registration successful! You have been registered for the event.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("events", eventService.getUpcomingEvents());
            model.addAttribute("errorMessage", e.getMessage());
            return "registrations/register";
        }
        
        return "redirect:/registrations/register";
    }

    @GetMapping("/{id}/delete")
    public String deleteRegistration(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            registrationService.removeParticipant(id);
            redirectAttributes.addFlashAttribute("successMessage", "Registration deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting registration: " + e.getMessage());
        }
        
        return "redirect:/registrations";
    }
}