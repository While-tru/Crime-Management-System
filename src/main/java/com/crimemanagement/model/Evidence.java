/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.model;

/**
 *
 * @author bhumiawasthi
 */
public class Evidence {
    private int evidenceId;
    private String type;
    private String description;
    private String dataCollected;
    private int crimeId;

    // Constructors
    public Evidence() {}

    public Evidence(int evidenceId, String type, String description, String dataCollected, int crimeId) {
        this.evidenceId = evidenceId;
        this.type = type;
        this.description = description;
        this.dataCollected = dataCollected;
        this.crimeId = crimeId;
    }

    // Getters and Setters
    public int getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(int evidenceId) {
        this.evidenceId = evidenceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataCollected() {
        return dataCollected;
    }

    public void setDataCollected(String dataCollected) {
        this.dataCollected = dataCollected;
    }

    public int getCrimeId() {
        return crimeId;
    }

    public void setCrimeId(int crimeId) {
        this.crimeId = crimeId;
    }
}
