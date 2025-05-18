package com.sanjot.inventory.services;

import com.sanjot.inventory.entity.User;
import com.sanjot.inventory.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    public User getLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public User saveUser(User user) {
        if (user.getId() == null) {
            user.setActive(true); // Mark as active for new users
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    public List<User> getAllActiveUsers() {
        return userRepository.findByActiveTrue();
    }


    public void saveUsers(User user) {
        user.setActive(true);
        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }
    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    public User findByUsername(String email) {
        return userRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));
    }



    public void deactivateUser(Long id) {
        User user = getUserById(id);
        if (user != null) {
            user.setActive(false);
            userRepository.save(user);
        }
    }

}
