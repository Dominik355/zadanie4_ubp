package com.zadanie4_ubp;

import com.zadanie4_ubp.storage.Converters;
import com.zadanie4_ubp.storage.DatabaseConnection;
import com.zadanie4_ubp.storage.User;
import com.zadanie4_ubp.storage.UserCredentials;
import com.zadanie4_ubp.storage.UserRegistration;
import com.zadanie4_ubp.storage.UserRepository;
import java.sql.SQLException;
import java.util.List;


public class Service {
    
    public static final int MIN_NAME_LENGTH = 4;
    public static final int MAX_NAME_LENGTH = 30;
    
    private DatabaseConnection dbConnection;
    private UserRepository userRepository;
    private PasswordService passwordService;
    private Converters converters;
   
    private static Service service;
    
    private Service() {
        this.dbConnection = new DatabaseConnection();
        this.userRepository = dbConnection.getUserRepository();
        this.passwordService = new PasswordService();
        this.converters = new Converters();
    }
    
    public void registerUser(UserRegistration registration) {
        if(!userRepository.existsByUsername(registration.getUsername())) {
            if(isRegistrationValid(registration)) {
                User newUser = converters.registrationToUser(registration);
                byte[] salt = passwordService.getNextSalt();
                byte[] password = passwordService.hash(registration.getPassword().toCharArray(), salt);
                newUser.setPassword(password);
                newUser.setSalt(salt);
                try {
                    userRepository.save(newUser);
                } catch (SQLException e) {
                    System.out.println("User could not be saved, message: " +e.getMessage());
                }
                System.out.println("User created: " + newUser.getUsername());
                PopUpWindow.showPopUp("User has been created");
            } else {
                PopUpWindow.showPopUp("User could not be created");
            }
        } else {
            PopUpWindow.showPopUp("Username " + registration.getUsername() + " is already taken");
        }
    }
    
    //zavola funkciu v PasswordService na porovnanie ulozeneho a zadaneho hesla
    //pokial je prihlasenie uspesne, vratim meno uzivatela
    public String login(UserCredentials credentials) {
        User user = userRepository.getUserByUsername(credentials.getUsername());
        if (user != null) {
            if (passwordService
                    .isExpectedPassword(
                            credentials.getPassword(), 
                            user.getSalt(), 
                            user.getPassword()
                    )) {
                return user.getUsername();
            }
        }
        return null;
    }
    
    //skontroluje ci meno, heslo vyhovuju a ci hesla sa zhoduju
    public boolean isRegistrationValid(UserRegistration registration) {
        if(!registration.getUsername().isEmpty()
                && registration.getUsername().length() >= MIN_NAME_LENGTH 
                && registration.getUsername().length() <= MAX_NAME_LENGTH) {
            
            if(registration.getPassword().equals(registration.getPasswordAgain())) {
                if(passwordService.isPasswordValid(registration.getPassword())) {
                    return true;
                } else {
                    PopUpWindow.showPopUp("Password does not match requirements, needed:\n"
                            + "at least 1 capital \n"
                            + "at least 1 small letter \n"
                            + "at least 1 digit \n"
                            + "not 3 same chars in a row \n"
                            + "or your password is globally well-known \n");
                }
            } else {
                PopUpWindow.showPopUp("Passwords do not match");
            }
        } else {
            PopUpWindow.showPopUp("Username does not match requirements");
        }
        return false;
    }
    
    public List<String> getUsers() {
        return userRepository.getAllUserNames();
    }
    
    //aby bola singleton instancia, takze bude iba 1 databazove spojenie,
    //ktore staci pri ukonceni programu uzavriet
    public static Service getInstance() {
        if (service == null) {
            service = new Service();
        }
        return service;
    }
    
    //vola sa pri ukonceni programu
    public void closeDbConnection() {
        this.dbConnection.closeConnection();
    }
    /*
    private void exampleUser() {
        this.registerUser(new UserRegistration(
                "Dominik222",
                "Dominik222",
                "Dominik222"
        ));
        System.out.println("users:");
        Connection conn;
         try {  
            conn = DriverManager.getConnection(DB_URL + System.getProperty("user.dir") + "/" + DB_FILE_NAME);  
             Statement stmt  = conn.createStatement();  
            ResultSet rs    = stmt.executeQuery("SELECT * FROM users");  
              
            // loop through the result set  
            while (rs.next()) {  
                System.out.println(rs.getString("username"));  
            }  
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }  
    }*/
    
}
