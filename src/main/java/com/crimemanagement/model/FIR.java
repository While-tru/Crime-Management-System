/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class FIR {
    private int id;
    private String description;  // Same as details - used for consistency
    private LocalDate dateFiled; // Same as date - used for consistency
    private LocalTime timeFiled; // Time when the FIR was filed
    private int officerId;
    private String status;
    private int caseId;
    
    /**
     * Constructor used by ViewFIRsFrame and database retrieval
     */
    public FIR(int id, int caseId, String description, LocalDate dateFiled) {
        this.id = id;
        this.caseId = caseId;
        this.description = description;
        this.dateFiled = dateFiled;
        this.timeFiled = LocalTime.now(); // Default to current time
    }
    
    /**
     * Constructor with time parameter
     */
    public FIR(int id, int caseId, String description, LocalDate dateFiled, LocalTime timeFiled) {
        this.id = id;
        this.caseId = caseId;
        this.description = description;
        this.dateFiled = dateFiled;
        this.timeFiled = timeFiled;
    }

    /**
     * Constructor used by FileFIRFrame
     */
    public FIR(int id, String description, LocalDate dateFiled, int officerId, String status) {
        this.id = id;
        this.description = description;
        this.dateFiled = dateFiled;
        this.timeFiled = LocalTime.now(); // Default to current time
        this.officerId = officerId;
        this.status = status;
    }
    
    /**
     * Constructor with time parameter
     */
    public FIR(int id, String description, LocalDate dateFiled, LocalTime timeFiled, int officerId, String status) {
        this.id = id;
        this.description = description;
        this.dateFiled = dateFiled;
        this.timeFiled = timeFiled;
        this.officerId = officerId;
        this.status = status;
    }

    // Default constructor
    public FIR() {
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateFiled() { return dateFiled; }
    public void setDateFiled(LocalDate dateFiled) { this.dateFiled = dateFiled; }

    public int getOfficerId() { return officerId; }
    public void setOfficerId(int officerId) { this.officerId = officerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getCaseId() { return caseId; }
    public void setCaseId(int caseId) { this.caseId = caseId; }

    // For backward compatibility
    public String getDetails() { return description; }
    public void setDetails(String details) { this.description = details; }

    public LocalDate getDate() { return dateFiled; }
    public void setDate(LocalDate date) { this.dateFiled = date; }
    
    public LocalTime getTimeFiled() { return timeFiled; }
    public void setTimeFiled(LocalTime timeFiled) { this.timeFiled = timeFiled; }
    
    // For backward compatibility
    public LocalTime getTime() { return timeFiled; }
    public void setTime(LocalTime time) { this.timeFiled = time; }
}

