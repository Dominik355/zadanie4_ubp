package com.zadanie4_ubp.storage;


public class UserRegistration {
    
    private String username;
    
    private String password;
    
    private String passwordAgain;

    public UserRegistration() {
    }

    public UserRegistration(String username, String password, String passwordAgain) {
        this.username = username;
        this.password = password;
        this.passwordAgain = passwordAgain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordAgain() {
        return passwordAgain;
    }

    public void setPasswordAgain(String passwordAgain) {
        this.passwordAgain = passwordAgain;
    }
    
}
