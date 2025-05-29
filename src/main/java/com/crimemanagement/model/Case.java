/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.model;

import java.time.LocalDate; // Import LocalDate

public class Case {
    private int id;
    private String title;
    private String description;
    private String status;
    private LocalDate dateStarted;  // Change to LocalDate type
    
    // Constructor
    public Case(int id, String title, String description, String status, LocalDate dateStarted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dateStarted = dateStarted;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDate getDateStarted() { return dateStarted; }  // Change getter to return LocalDate

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setDateStarted(LocalDate dateStarted) { this.dateStarted = dateStarted; }  // Change setter to accept LocalDate
    
    // Other fields related to crime
    private String crimeType;
    private String crimeLocation;
    
    public Case() {
        
    }

    public String getCrimeType() { return crimeType; }
    public void setCrimeType(String crimeType) { this.crimeType = crimeType; }

    public String getCrimeLocation() { return crimeLocation; }
    public void setCrimeLocation(String crimeLocation) { this.crimeLocation = crimeLocation; }

}
