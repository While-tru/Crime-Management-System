/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.model;

public class Complaint {
    private int id;
    private String complainantName;
    private String description;
    private String status;
    private String caseId;
    private String witness;

    // Constructor
    public Complaint(int id, String complainantName, String description, String status, String caseId, String witness) {
        this.id = id;
        this.complainantName = complainantName;
        this.description = description;
        this.status = status;
        this.caseId = caseId;
        this.witness = witness;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getComplainantName() { return complainantName; }
    public void setComplainantName(String complainantName) { this.complainantName = complainantName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCaseId() { return caseId; }
    public void setCaseId(String caseId) { this.caseId = caseId; }

    public String getWitness() { return witness; }
    public void setWitness(String witness) { this.witness = witness; }
}

