package com.zadanie4_ubp.storage;

import static com.zadanie4_ubp.storage.DatabaseConnection.DB_FILE_NAME;
import static com.zadanie4_ubp.storage.DatabaseConnection.DB_URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UserRepository {
    
    private static final String SAVE = "INSERT INTO users(username,password,salt) VALUES(?,?,?)";
    private static final String GET_ALL_USERNAMES = "SELECT users.username FROM users GROUP BY users.username";
    private static final String GET_USER = "SELECT * FROM users WHERE username = ?";
    
    private Connection connection;
    
    public UserRepository(Connection connection) {
        this.connection = connection;
    }
    
    public void save(User user) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SAVE);
        ps.setString(1, user.getUsername());
        ps.setBytes(2, user.getPassword());
        ps.setBytes(3, user.getSalt());
        ps.executeUpdate();
        ps.close();
    }
    
    public List<String> getAllUserNames() {
        List<String> users = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(GET_ALL_USERNAMES);
            ResultSet result = ps.executeQuery();
            while(result.next()) {
                System.out.println("user found");
                users.add(result.getString("username"));
            }
            ps.close();
        } catch (SQLException e) {
            System.out.println("Problem with selecting users, message: " +e.getMessage());
        }
        return users;
    }
    
    public User getUserByUsername(String username) {
        try {
            PreparedStatement ps = connection.prepareStatement(GET_USER);
            ps.setString(1, username);
            ResultSet result = ps.executeQuery();
            return new User(
                    result.getString("username"),
                    result.getBytes("password"),
                    result.getBytes("salt")
            );
        } catch (SQLException e) {
            System.out.println("Problem with finding user, message: " + e.getMessage());
            e.printStackTrace();
            // ak nic nenajde, tak vrati resultset==closed, zevraj to robi len pre SQLite
        }
        return null;
    }
    
    public boolean existsByUsername(String username) {
        if(getUserByUsername(username) != null) {
            return true;
        } 
        return false;
    }
    
}
