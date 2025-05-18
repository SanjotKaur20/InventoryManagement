package com.sanjot.inventory.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String storedPassword = "$2a$10$2EcubYkMXXz4Puv0eXiP0.rfHgajMa01GGzQrcrKbPavnFN37vrha"; 

        // Raw password entered by admin
        String rawPassword = "admin123"; 

        boolean isMatch = encoder.matches(rawPassword, storedPassword);

        System.out.println("Password Match: " + isMatch); // Should print "true"
    }
}
