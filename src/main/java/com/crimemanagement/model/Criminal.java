package com.crimemanagement.model;

import java.time.LocalDate;

public class Criminal {
    private int id;
    private String name;
    private String address;
    private String status; // e.g., "In Custody", "Released", "Wanted"
     
    // Constructors
    public Criminal() {
    }

    public Criminal(int id, String name, String address, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.address = address;
        
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String Address) {
        this.address = address;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String Address) {
        this.status = status;
    }

    

    @Override
    public String toString() {
        return "Criminal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", Address='" + address + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public void setFName(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setLName(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setRecordStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}