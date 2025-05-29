/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.crimemanagement.dao.CaseDAO;
import com.crimemanagement.dao.CourtDAO;
import com.crimemanagement.dao.JudgeDAO;
import com.crimemanagement.model.Case;
import com.crimemanagement.model.Court;
import com.crimemanagement.model.Judge;

public class JudgeCaseFrame extends JFrame {
    private JTable caseTable;
    private JTable judgeTable;
    private JTable courtTable;

    public JudgeCaseFrame() {
        super("All Cases & Administration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // --- Tab 1: Cases + Crime Details ---
        caseTable = new JTable();
        tabs.addTab("Cases & Crime", new JScrollPane(caseTable));
        loadCases();

        // --- Tab 2: Judge Info ---
        judgeTable = new JTable();
        tabs.addTab("Judges", new JScrollPane(judgeTable));
        loadJudges();

        // --- Tab 3: Court Info ---
        courtTable = new JTable();
        tabs.addTab("Courts", new JScrollPane(courtTable));
        loadCourts();

        // Save button to persist any edits
        JButton saveBtn = new JButton("Save Changes");
        saveBtn.addActionListener(e -> {
            try {
                System.out.println("Starting to save changes...");
                updateCase();
                updateJudges();
                updateCourts();
                JOptionPane.showMessageDialog(this, "All changes saved.");
                System.out.println("All changes saved successfully.");
            } catch (Exception ex) {
                System.err.println("Error saving changes: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error saving changes: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Back button
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            dispose(); // Close this frame
        });
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backBtn);
        buttonPanel.add(saveBtn);

        // Layout
        add(tabs, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadCases() {
        System.out.println("Loading cases with crime data...");
        List<Case> list = CaseDAO.getCasesWithCrime();  
        DefaultTableModel m = new DefaultTableModel(
            new String[]{"Case ID", "Status", "Date Started", "Crime Type", "Crime Location"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Case ID not editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Integer.class; // Case ID is integer
                } else if (columnIndex == 2) {
                    return java.time.LocalDate.class; // Date is LocalDate
                }
                return String.class; // Other columns are strings
            }
        };
        
        System.out.println("Found " + list.size() + " cases");
        
        for (Case c : list) {
            try {
                m.addRow(new Object[]{
                    c.getId(),
                    c.getStatus() != null ? c.getStatus() : "Pending",
                    c.getDateStarted(),
                    c.getCrimeType() != null ? c.getCrimeType() : "Unknown",
                    c.getCrimeLocation() != null ? c.getCrimeLocation() : "Unknown"
                });
                
                System.out.println("Added case to table: ID=" + c.getId() + 
                                  ", Status=" + c.getStatus() + 
                                  ", Date=" + c.getDateStarted() + 
                                  ", Type=" + c.getCrimeType() + 
                                  ", Location=" + c.getCrimeLocation());
            } catch (Exception e) {
                System.err.println("Error adding case to table: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        caseTable.setModel(m);
        
        // Set left alignment for Case ID column
        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        caseTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        
        System.out.println("Case table model updated with " + m.getRowCount() + " rows");
    }

    private void loadJudges() {
        List<Judge> list = JudgeDAO.getAllJudges();
        DefaultTableModel m = new DefaultTableModel(
            new String[]{"Judge ID", "First Name", "Last Name", "Experience", "Court Assigned"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return c != 0; }
        };
        for (Judge j : list) {
            m.addRow(new Object[]{
                j.getId(),
                j.getFirstName(),
                j.getLastName(),
                j.getexperience(),
                j.getCourt()
            });
        }
        judgeTable.setModel(m);
        
        // Set left alignment for Judge ID column
        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        judgeTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
    }

    private void loadCourts() {
        List<Court> list = CourtDAO.getAllCourts();
        DefaultTableModel m = new DefaultTableModel(
            new String[]{"Court ID", "Name", "Location", "Verdict"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return c != 0; }
        };
        for (Court c : list) {
            m.addRow(new Object[]{
                c.getId(),
                c.getName(),
                c.getLocation(),
                c.getVerdict()
            });
        }
        courtTable.setModel(m);
        
        // Set left alignment for Court ID column
        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        courtTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
    }
    
    private void updateCase(){
        System.out.println("Updating cases...");
        DefaultTableModel m = (DefaultTableModel) caseTable.getModel();
        boolean anyUpdates = false;
        
        for (int i = 0; i < m.getRowCount(); i++) {
            try {
                Case c = new Case();
                int id = (int) m.getValueAt(i, 0);
                
                // Handle potential ClassCastException for status
                Object statusObj = m.getValueAt(i, 1);
                String status = statusObj != null ? statusObj.toString() : "Pending";
                
                // Handle potential ClassCastException for date
                LocalDate dateStarted = null;
                Object dateObj = m.getValueAt(i, 2);
                if (dateObj instanceof LocalDate) {
                    dateStarted = (LocalDate) dateObj;
                } else if (dateObj instanceof java.sql.Date) {
                    dateStarted = ((java.sql.Date) dateObj).toLocalDate();
                } else if (dateObj instanceof java.util.Date) {
                    dateStarted = ((java.util.Date) dateObj).toInstant()
                                  .atZone(java.time.ZoneId.systemDefault())
                                  .toLocalDate();
                } else if (dateObj != null) {
                    try {
                        dateStarted = LocalDate.parse(dateObj.toString());
                    } catch (Exception e) {
                        System.err.println("Error parsing date: " + dateObj + ". Using current date.");
                        dateStarted = LocalDate.now();
                    }
                } else {
                    dateStarted = LocalDate.now();
                }
                
                // Handle potential ClassCastException for crime type
                Object typeObj = m.getValueAt(i, 3);
                String crimeType = typeObj != null ? typeObj.toString() : "Unknown";
                
                // Handle potential ClassCastException for crime location
                Object locationObj = m.getValueAt(i, 4);
                String crimeLocation = locationObj != null ? locationObj.toString() : "Unknown";
                
                c.setId(id);
                c.setStatus(status);
                c.setDateStarted(dateStarted);
                c.setCrimeType(crimeType);
                c.setCrimeLocation(crimeLocation);
                
                System.out.println("Updating case: ID=" + id + ", Status=" + status + 
                                  ", Date=" + dateStarted + ", Type=" + crimeType + 
                                  ", Location=" + crimeLocation);
                
                CaseDAO.updateCase(c);
                anyUpdates = true;
            } catch (Exception e) {
                System.err.println("Error updating case at row " + i + ": " + e.getMessage());
                e.printStackTrace();
                
                // Show error but continue with other updates
                JOptionPane.showMessageDialog(this, 
                    "Error updating case at row " + (i+1) + ": " + e.getMessage() + 
                    "\nContinuing with other updates.", 
                    "Update Warning", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
        
        if (anyUpdates) {
            System.out.println("Finished updating cases. Reloading data...");
            loadCases(); // Reload the data to show the updates
        } else {
            System.out.println("No cases were updated.");
        }
    }

    private void updateJudges() {
        System.out.println("Updating judges...");
        DefaultTableModel m = (DefaultTableModel) judgeTable.getModel();
        for (int i = 0; i < m.getRowCount(); i++) {
            try {
                Judge j = new Judge();
                int id = (int) m.getValueAt(i, 0);
                String firstName = (String) m.getValueAt(i, 1);
                String lastName = (String) m.getValueAt(i, 2);
                int experience = 0;
                
                // Handle potential ClassCastException for experience
                Object expObj = m.getValueAt(i, 3);
                if (expObj instanceof Integer) {
                    experience = (Integer) expObj;
                } else if (expObj instanceof String) {
                    experience = Integer.parseInt((String) expObj);
                }
                
                String court = (String) m.getValueAt(i, 4);
                
                j.setId(id);
                j.setFirstName(firstName);
                j.setLastName(lastName);
                j.setexperience(experience);
                j.setCourt(court);
                
                System.out.println("Updating judge: ID=" + id + ", Name=" + firstName + " " + lastName + 
                                  ", Experience=" + experience + ", Court=" + court);
                
                JudgeDAO.updateJudge(j);
            } catch (Exception e) {
                System.err.println("Error updating judge at row " + i + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Finished updating judges.");
    }

    private void updateCourts() {
        System.out.println("Updating courts...");
        DefaultTableModel m = (DefaultTableModel) courtTable.getModel();
        for (int i = 0; i < m.getRowCount(); i++) {
            try {
                Court c = new Court();
                int id = (int) m.getValueAt(i, 0);
                String name = (String) m.getValueAt(i, 1);
                String location = (String) m.getValueAt(i, 2);
                String verdict = (String) m.getValueAt(i, 3);
                
                c.setId(id);
                c.setName(name);
                c.setLocation(location);
                c.setVerdict(verdict);
                
                System.out.println("Updating court: ID=" + id + ", Name=" + name + 
                                  ", Location=" + location + ", Verdict=" + verdict);
                
                CourtDAO.updateCourt(c);
            } catch (Exception e) {
                System.err.println("Error updating court at row " + i + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Finished updating courts.");
    }
}

