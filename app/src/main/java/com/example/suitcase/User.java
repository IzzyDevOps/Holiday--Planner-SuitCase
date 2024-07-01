package com.example.suitcase;

public class User {

    private String firstName;
    private String lastName;
    private static String email;
    private String password;
    private String contactNumber;

    // Constructor to initialize the User object with the provided data
    public User(String firstName, String lastName, String email, String password, String contactNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
    }

    // Default constructor with no parameters
    public User() {
    }

    // Getter and Setter methods for each property

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}

