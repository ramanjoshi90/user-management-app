package com.usermgmt.model;

public class User {
    private String email; // acts as the ID
    private String name;
    private String dob;
    private String password;
    private String phone;
    private String gender;
    private String address;

    public User(String email, String name, String dob, String password, String phone, String gender, String address) {
        this.email = email;
        this.name = name;
        this.dob = dob;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
    }

    // Getters are required for Gson to convert this to JSON
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getDob() { return dob; }
    public String getPhone() { return phone; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    // Don't expose password in getter if possible, but needed for internal logic
    public String getPassword() { return password; }
}