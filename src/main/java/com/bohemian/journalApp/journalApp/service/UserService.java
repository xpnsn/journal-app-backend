package com.bohemian.journalApp.journalApp.service;

import com.bohemian.journalApp.journalApp.entity.JournalEntry;
import com.bohemian.journalApp.journalApp.entity.User;
import com.bohemian.journalApp.journalApp.repository.JournalEntryRepository;
import com.bohemian.journalApp.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) { return userRepository.findByUsername(username).orElse(null);}

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public void saveNewUser(User user) {
        user.setPassword(passwordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void deleteById(ObjectId id) {
        userRepository.deleteById(id);
    }

    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
    }
}
