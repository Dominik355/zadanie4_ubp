package com.zadanie4_ubp.storage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CollectionCertStoreParameters;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;


public class DatabaseConnection {
    
    public static final String DB_FILE_NAME = "zadanie4DB.db";
    public static final String DB_URL = "jdbc:sqlite:";
    
    private Connection connection;
    private String projectDirectory = System.getProperty("user.dir");
    
    public DatabaseConnection() {
        this.initialize();
    }
    
    private void initialize() {
        /*
        try {
            File file = new File(projectDirectory + "/" + DB_FILE_NAME);
            if (file.createNewFile()) {
                System.out.println("File for Database created");
            } else {
                System.out.println("File for Database can not be created, it already exists");
            }
        } catch (IOException e) {
            System.err.println("Error creating database file, message: " + e);
        }*/
        this.connect();
        this.createUserTable();
    }
    
    public void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL + projectDirectory + "/" + DB_FILE_NAME);
            System.out.println("Connection to database has been established");
        } catch (SQLException e) {
            System.out.println("Error at creating database connection, message: " + e.getMessage());
        }
    }
    
    public void createUserTable() {
        String userTable = "CREATE TABLE IF NOT EXISTS users (\n" 
                + " username text NOT NULL,\n" 
                + " password BLOB NOT NULL,\n" 
                + " salt BLOB NOT NULL,\n"
                + " PRIMARY KEY (username)\n" 
                + ");";
        
        try {
            if(this.connection != null) {
                Statement stmt = connection.createStatement();
                stmt.execute(userTable);
                System.out.println("Users table created/udated");
                stmt.close();
            }
        } catch (SQLException e) {
            System.out.println("Error creating users table, message: " + e.getMessage());
        }
    }
    
    public UserRepository getUserRepository() {
        return new UserRepository(this.connection);
    }
    
    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            System.out.println("Problem while closing DB connection, message: " + e.getMessage());
        }
    }
    
}
