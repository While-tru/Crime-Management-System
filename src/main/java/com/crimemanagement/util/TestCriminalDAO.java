package com.crimemanagement.util;

import com.crimemanagement.dao.CriminalDAO;
import com.crimemanagement.model.Criminal;
import java.util.List;

public class TestCriminalDAO {
    public static void main(String[] args) {
        System.out.println("Testing CriminalDAO...");
        
        // Test getAllCriminals
        List<Criminal> criminals = CriminalDAO.getAllCriminals();
        System.out.println("Found " + criminals.size() + " criminals");
        
        for (Criminal c : criminals) {
            System.out.println("Criminal ID: " + c.getId());
            System.out.println("  Name: " + c.getName());
            System.out.println("  Status: " + c.getStatus());
            System.out.println("  Address: " + c.getAddress());
            System.out.println();
        }
        
        // Test creating a new criminal
        if (criminals.isEmpty()) {
            Criminal newCriminal = new Criminal();
            newCriminal.setName("John Doe");
            newCriminal.setStatus("Wanted");
            
            boolean created = CriminalDAO.createCriminal(newCriminal);
            System.out.println("Created new criminal: " + created);
            
            if (created) {
                System.out.println("New criminal ID: " + newCriminal.getId());
            }
        }
        
        // Test getting a criminal by ID
        if (!criminals.isEmpty()) {
            int id = criminals.get(0).getId();
            Criminal criminal = CriminalDAO.getCriminalById(id);
            
            if (criminal != null) {
                System.out.println("Found criminal by ID " + id + ": " + criminal.getName());
            } else {
                System.out.println("Could not find criminal with ID " + id);
            }
        }
    }
}