package com.techfest.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techfest.model.Event;
import com.techfest.model.Registration;
import com.techfest.model.Winner;
import com.techfest.service.EventService;
import com.techfest.service.RegistrationService;
import com.techfest.service.WinnerService;

@Controller
@RequestMapping("/winners")
public class WinnerController {

    private final WinnerService winnerService;
    private final EventService eventService;
    private final RegistrationService registrationService;

    @Autowired
    public WinnerController(WinnerService winnerService, 
                           EventService eventService, 
                           RegistrationService registrationService) {
        this.winnerService = winnerService;
        this.eventService = eventService;
        this.registrationService = registrationService;
    }

    @GetMapping
    public String listWinners(@RequestParam(value = "eventId", required = false) Integer eventId, Model model) {
        List<Winner> winners;
        
        if (eventId != null) {
            winners = winnerService.getByEvent(eventId);
            Event event = eventService.get(eventId);
            model.addAttribute("event", event);
            
            // Get registrations for this event to map roll numbers to names
            List<Registration> registrations = registrationService.getByEvent(eventId);
            Map<String, String> rollToNameMap = registrations.stream()
                .collect(Collectors.toMap(Registration::getRollNo, Registration::getName));
            model.addAttribute("rollToNameMap", rollToNameMap);
        } else {
            winners = winnerService.getAll();
            
            // For all winners, we need to get all registrations to create the mapping
            List<Registration> allRegistrations = registrationService.getAll();
            Map<String, String> rollToNameMap = allRegistrations.stream()
                .collect(Collectors.toMap(Registration::getRollNo, Registration::getName));
            model.addAttribute("rollToNameMap", rollToNameMap);
        }
        
        model.addAttribute("winners", winners);
        model.addAttribute("events", eventService.getAll());
        return "winners/list";
    }

    @GetMapping("/declare")
    public String showDeclareWinnersForm(@RequestParam(value = "eventId", required = false) Integer eventId, Model model) {
        List<Event> events = eventService.getAll();
        model.addAttribute("events", events);
        
        if (eventId != null) {
            Event event = eventService.get(eventId);
            List<Registration> registrations = registrationService.getByEvent(eventId);
            List<Winner> existingWinners = winnerService.getByEvent(eventId);
            
            model.addAttribute("selectedEvent", event);
            model.addAttribute("registrations", registrations);
            model.addAttribute("existingWinners", existingWinners);
        }
        
        return "winners/declare";
    }

    @PostMapping("/declare")
    public String declareWinners(@RequestParam("eventId") int eventId,
                               @RequestParam("firstPlace") String firstPlace,
                               @RequestParam("secondPlace") String secondPlace,
                               @RequestParam("thirdPlace") String thirdPlace,
                               RedirectAttributes redirectAttributes) {
        try {
            winnerService.setTop3(eventId, firstPlace, secondPlace, thirdPlace);
            redirectAttributes.addFlashAttribute("successMessage", "Winners declared successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/winners/declare?eventId=" + eventId;
        }
        
        return "redirect:/winners?eventId=" + eventId;
    }

    @GetMapping("/{id}/delete")
    public String deleteWinner(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            winnerService.deleteWinner(id);
            redirectAttributes.addFlashAttribute("successMessage", "Winner deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting winner: " + e.getMessage());
        }
        
        return "redirect:/winners";
    }
}