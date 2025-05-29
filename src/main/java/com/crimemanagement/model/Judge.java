/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.model;

public class Judge {
    private int id;
    private String firstName;
    private String lastName;
    private String court;
    private int experience;

    public Judge() {}

    public Judge(int id, String firstName, String lastName, String court, int experience) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.court = court;
        this.experience = experience;
    }
    
    public int getexperience() { return experience; }
    public void setexperience(int experience) { this.experience = experience; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    // For backward compatibility
    public String getName() {
        return firstName + " " + lastName;
    }

    public void setName(String name) {
        if (name != null && name.contains(" ")) {
            String[] parts = name.split(" ", 2);
            this.firstName = parts[0];
            this.lastName = parts[1];
        } else {
            this.firstName = name;
            this.lastName = "";
        }
    }

    public String getCourt() {
        return court;
    }

    public void setCourt(String court) {
        this.court = court;
    }
}


