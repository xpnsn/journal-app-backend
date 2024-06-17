package com.bohemian.journalApp.journalApp.service;

import com.bohemian.journalApp.journalApp.entity.JournalEntry;
import com.bohemian.journalApp.journalApp.entity.User;
import com.bohemian.journalApp.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JournalEntryService {

    JournalEntryRepository journalEntryRepository;
    UserService userService;


    public JournalEntryService(JournalEntryRepository journalEntryRepository, UserService userService) {
        this.journalEntryRepository = journalEntryRepository;
        this.userService = userService;
    }

    public List<JournalEntry> getJournalEntryByUsername(String username) {
        User user = userService.getUserByUsername(username);
        return user != null ? user.getJournalEntries() : null;
    }

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username) {
        try {
            User user = userService.getUserByUsername(username);
            journalEntry.setDate(LocalDateTime.now());
            user.getJournalEntries().add(journalEntry);
            journalEntryRepository.save(journalEntry);
            userService.saveUser(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveEntry(JournalEntry journalEntry) {
        journalEntryRepository.save(journalEntry);
    }


    public Optional<JournalEntry> findEntryById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    public void deleteById(ObjectId id, String username) {
        User user = userService.getUserByUsername(username);
        journalEntryRepository.deleteById(id);
        user.getJournalEntries().removeIf(x -> x.getId().equals(id));
        userService.saveUser(user);
    }
}
