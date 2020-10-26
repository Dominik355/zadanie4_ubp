package com.zadanie4_ubp.storage;


public class UserCredentials {
    
    private String username;
    
    private char[] password;

    public UserCredentials(String username, char[] password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
  
    
}
