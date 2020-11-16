package com.zadanie4_ubp;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.passay.CharacterRule;
import org.passay.DictionaryRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RepeatCharactersRule;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.passay.dictionary.ArrayWordList;
import org.passay.dictionary.Dictionary;
import org.passay.dictionary.WordListDictionary;


public class PasswordService {
    
    private static final String PASSWORD_DICTIONARY_FILE_NAME = "passDic.txt";
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 30;
    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 1000;
    private static final int KEY_LENGTH = 256;
    private PasswordValidator passwordValidator;
    
    public PasswordService() {
        this.passwordValidator = new PasswordValidator(
                new LengthRule(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH), //urcuje minimalnu a maximalnu dlzku hesla
                new CharacterRule(EnglishCharacterData.Digit, 1), // aspon 1 cislica
                new CharacterRule(EnglishCharacterData.LowerCase, 1), //aspon 1 male pismeno
                new CharacterRule(EnglishCharacterData.UpperCase, 1), //aspon 1 velke pismeno
                new WhitespaceRule(), //ziadne whitespace
                new RepeatCharactersRule(3), //max dlzka zasebu iducich rovnakych znakov je 3
                new DictionaryRule(new WordListDictionary(loadDictionaryFromFile())) // validacia proti slovniku
        );
    }

    //vrati 16 bajtov dlhy nahodny salt
    public byte[] getNextSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    //vrati zahashovane heslo pomocou saltu
    //pouzita instancia PBKDF2WithHmacSHA256
    //PBKDF2 - password-based-key-derivative-function, a 2 je verzia
    //HMACSHA256 - hashovaci algoritmus konstruovany z SHA256
    // pri generovani je zadefinovane pocet iteraci+dlzku kluca
    public byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    //vrati true ak sa zahashovane heslo a heslo+salt poslane do hashovacej funkcie zhoduju
    public boolean isExpectedPassword(char[] password, byte[] salt, byte[] expectedHash) {
        byte[] pwdHash = hash(password, salt);
        Arrays.fill(password, Character.MIN_VALUE);
        if (pwdHash.length != expectedHash.length) {
            return false;
        }
        for (int i = 0; i < pwdHash.length; i++) {
            if (pwdHash[i] != expectedHash[i]) return false;
        }
        return true;
    }
    
    public boolean isPasswordValid(String password){
        RuleResult result = passwordValidator.validate(new PasswordData(password));
        if(result.isValid()) {
            return true;
        } else {
            System.out.println("Password is not valid according to rules, Result message: ");
            System.out.println("    " + passwordValidator.getMessages(result));
            return false;
        }
    }
    
    public ArrayWordList loadDictionaryFromFile() {
        List<String> words = Collections.emptyList();
        try {
            words = Files.readAllLines(Paths.get(PASSWORD_DICTIONARY_FILE_NAME));
        } catch (Exception e) {
            System.out.println("File passDic.txt could not be found. Using default password Dictionary");
            words.add("defaultPassword"); //
        }
        return new ArrayWordList(words.toArray(new String[0]), false);
    }
    
    /*
    //restrikcie na heslo :
    // aspon 1 cislica
    //aspon 1 male pismeno
    // aspon 1 velke pismeno
    // ziadne znaky z definovaneho regexu
    public boolean isPasswordValid(String password) {
        if(password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
          return false;
      }
      
      int uppercase = 0;
      int lowercase = 0;
      int digits = 0;
      int specialCharacters = 0;
      char[] passwordChars = password.toCharArray();
      
      for (int index = 0; index < password.length(); index++) {
          if(Character.isUpperCase(passwordChars[index])) {
              uppercase += 1;
          }
          if(Character.isLowerCase(passwordChars[index])) {
              lowercase += 1;
          }
          if(Character.isDigit(passwordChars[index])) {
              digits += 1;
          }
      }
      if(password.matches("(?=.[!@#\\$%\\^&]) ")) {
          specialCharacters += 1;
      }
      
      return (uppercase >= 1) && (lowercase >= 1) && (digits >= 1) && (specialCharacters == 0);
    }
    */
}
