package com.zadanie4_ubp.storage;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {
    
    private String username;
    
    private byte[] password;
    
    private byte[] salt;

    public User(String username, byte[] password, byte[] salt) {
        this.username = username;
        this.password = password;
        this.salt = salt;
    }
    
    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    } 
    
}
