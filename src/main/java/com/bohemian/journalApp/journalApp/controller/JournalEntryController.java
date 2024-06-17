package com.bohemian.journalApp.journalApp.controller;


import com.bohemian.journalApp.journalApp.entity.JournalEntry;
import com.bohemian.journalApp.journalApp.entity.User;
import com.bohemian.journalApp.journalApp.service.JournalEntryService;
import com.bohemian.journalApp.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    JournalEntryService journalEntryService;
    UserService userService;

    public JournalEntryController(JournalEntryService journalEntryService, UserService userService) {
        this.journalEntryService = journalEntryService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getJournalsOfUser() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        List<JournalEntry> journalEntry = journalEntryService.getJournalEntryByUsername(username);

        if(journalEntry != null) {
            return new ResponseEntity<>(journalEntry, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @GetMapping("id/{id}")
    public ResponseEntity<?> getJournalEntryById(@PathVariable ObjectId id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        List<JournalEntry> userJournalEntry = user.getJournalEntries()
                .stream().filter(x -> x.getId().equals(id)).toList();

        if(!userJournalEntry.isEmpty()) {
            return new ResponseEntity<>(userJournalEntry, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

//        return journalEntry.map(entry -> new ResponseEntity<>(entry, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> createJournalEntry(@RequestBody JournalEntry journalEntry) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            journalEntryService.saveEntry(journalEntry, username);
            return new ResponseEntity<>(journalEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("id/{id}")
    public ResponseEntity<?> updateJournalEntry(@PathVariable ObjectId id, @RequestBody JournalEntry journalEntry) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        List<JournalEntry> entry = user.getJournalEntries()
                .stream().filter(x -> x.getId().equals(id)).toList();

        if(!entry.isEmpty()) {
            entry.getFirst().setTitle(journalEntry.getTitle() != null ? journalEntry.getTitle() : entry.getFirst().getTitle());
            entry.getFirst().setContent(journalEntry.getContent() != null ? journalEntry.getContent() : entry.getFirst().getContent());
            journalEntryService.saveEntry(entry.getFirst());
            return new ResponseEntity<>(entry.getFirst(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{id}")
    public ResponseEntity<?> deleteJournalEntry(@PathVariable ObjectId id) {String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        List<JournalEntry> entry = user.getJournalEntries()
                .stream().filter(x -> x.getId().equals(id)).toList();

        if (!entry.isEmpty()) {
            journalEntryService.deleteById(id, username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
