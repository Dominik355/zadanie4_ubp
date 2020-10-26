package com.zadanie4_ubp.storage;


public class Converters {
    
    public User registrationToUser(UserRegistration registration) {
        return new User(registration.getUsername());
    }
    
}
