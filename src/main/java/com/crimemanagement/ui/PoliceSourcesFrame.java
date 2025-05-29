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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.crimemanagement.dao.CriminalDAO;
import com.crimemanagement.dao.JailDAO;
import com.crimemanagement.dao.PoliceOfficerDAO;
import com.crimemanagement.dao.PoliceStationDAO;
import com.crimemanagement.model.Criminal;
import com.crimemanagement.model.Jail;
import com.crimemanagement.model.PoliceOfficer;
import com.crimemanagement.model.PoliceStation;
import javax.swing.JLabel;

public class PoliceSourcesFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel policeStationPanel;
    private JPanel policeOfficerPanel;
    private JPanel jailPanel;
    private JPanel criminalPanel;

    public PoliceSourcesFrame() {
        setTitle("Police Sources Information");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Verify database schema before loading data
        System.out.println("Verifying database schema...");
        com.crimemanagement.util.DBConnection.verifyDatabaseSchema();

        tabbedPane = new JTabbedPane();

        try {
            policeStationPanel = createPoliceStationPanel();
            policeOfficerPanel = createPoliceOfficerPanel();
            jailPanel = createJailPanel();
            criminalPanel = createCriminalPanel();

            tabbedPane.addTab("Police Stations", policeStationPanel);
            tabbedPane.addTab("Police Officers", policeOfficerPanel);
            tabbedPane.addTab("Jails", jailPanel);
            tabbedPane.addTab("Criminals", criminalPanel);
        } catch (Exception ex) {
            System.err.println("Error creating panels: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }

        // Add back button at the bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose(); // Close this frame
        });
        bottomPanel.add(backButton);
        
        add(tabbedPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createPoliceStationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Column names match your model
        String[] cols = {"Station ID", "Name", "Location", "Contact"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return col != 0; // allow editing all but ID
            }
        };
        JTable table = new JTable(model);
        
        try {
            // Load data
            System.out.println("Attempting to load police stations...");
            List<PoliceStation> stations = PoliceStationDAO.getAllPoliceStation();
            
            if (stations == null) {
                System.err.println("ERROR: stations list is null");
                JOptionPane.showMessageDialog(this, 
                    "Failed to load police stations. See console for details.", 
                    "Data Error", 
                    JOptionPane.ERROR_MESSAGE);
                return panel;
            }
            
            System.out.println("Loaded " + stations.size() + " police stations");
            
            for (PoliceStation station : stations) {
                if (station == null) {
                    System.err.println("WARNING: Null station object in list");
                    continue;
                }
                
                try {
                    model.addRow(new Object[]{
                        station.getStationId(),
                        station.getName(),
                        station.getLocation(),
                        station.getContact()
                    });
                    System.out.println("Added station: " + station.getName());
                } catch (Exception ex) {
                    System.err.println("Error adding station to table: " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            System.err.println("Error in createPoliceStationPanel: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading police stations: " + ex.getMessage(), 
                "Data Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Save button to persist any edits back to DB
        JButton saveBtn = new JButton("Save Station Changes");
        saveBtn.addActionListener(e -> {
            try {
                // Stop cell editing if any cell is being edited
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }
                
                boolean allSuccess = true;
                
                for (int i = 0; i < model.getRowCount(); i++) {
                    Object idObj = model.getValueAt(i, 0);
                    int id;
                    if (idObj instanceof Integer) {
                        id = (Integer) idObj;
                    } else {
                        try {
                            id = Integer.parseInt(idObj.toString());
                        } catch (NumberFormatException ex) {
                            System.err.println("Invalid station ID format: " + idObj);
                            continue; // Skip this row
                        }
                    }
                    String name = String.valueOf(model.getValueAt(i, 1));
                    String location = String.valueOf(model.getValueAt(i, 2));
                    String contact = String.valueOf(model.getValueAt(i, 3));
                    
                    PoliceStation station = new PoliceStation();
                    station.setStationId(id);
                    station.setName(name);
                    station.setLocation(location);
                    station.setContact(contact);
                    
                    boolean success = PoliceStationDAO.updatePoliceStation(station);
                    if (!success) {
                        allSuccess = false;
                        System.err.println("Failed to update station ID: " + id);
                    }
                }
                
                if (allSuccess) {
                    JOptionPane.showMessageDialog(this, "All station updates saved successfully!");
                    // Refresh the data
                    List<PoliceStation> stations = PoliceStationDAO.getAllPoliceStation();
                    model.setRowCount(0);
                    for (PoliceStation station : stations) {
                        model.addRow(new Object[]{
                            station.getStationId(),
                            station.getName(),
                            station.getLocation(),
                            station.getContact()
                        });
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Some station updates failed. Check console for details.", 
                                                 "Update Warning", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating police stations: " + ex.getMessage(), 
                                             "Update Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        panel.add(saveBtn, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createPoliceOfficerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Column names match your model
        String[] cols = {"Officer ID", "Name", "Rank", "Station"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return col != 0; // allow editing all but ID
            }
        };
        JTable table = new JTable(model);

        try {
            // Load data
            System.out.println("Loading police officers...");
            List<PoliceOfficer> officers = PoliceOfficerDAO.getAllPoliceOfficers();
            
            if (officers == null) {
                System.err.println("ERROR: officers list is null");
                JOptionPane.showMessageDialog(this, 
                    "Failed to load police officers. See console for details.", 
                    "Data Error", 
                    JOptionPane.ERROR_MESSAGE);
                return panel;
            }
            
            System.out.println("Received " + officers.size() + " police officers");
            
            for (PoliceOfficer o : officers) {
                if (o == null) {
                    System.err.println("WARNING: Null officer object in list");
                    continue;
                }
                
                try {
                    System.out.println("Adding officer to table: ID=" + o.getId() + ", Name=" + o.getName() + 
                                      ", Rank=" + o.getRank() + ", Station=" + o.getStation());
                    
                    model.addRow(new Object[]{
                        o.getId(),
                        o.getName(),
                        o.getRank(),
                        o.getStation()
                    });
                } catch (Exception ex) {
                    System.err.println("Error adding officer to table: " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            System.err.println("Error in createPoliceOfficerPanel: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading police officers: " + ex.getMessage(), 
                "Data Error", 
                JOptionPane.ERROR_MESSAGE);
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Save button to persist any edits back to DB
        JButton saveBtn = new JButton("Save Officer Changes");
        saveBtn.addActionListener(e -> {
            try {
                // Stop cell editing if any cell is being edited
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }
                
                boolean allSuccess = true;
                
                for (int i = 0; i < model.getRowCount(); i++) {
                    Object idObj = model.getValueAt(i, 0);
                    int id;
                    if (idObj instanceof Integer) {
                        id = (Integer) idObj;
                    } else {
                        try {
                            id = Integer.parseInt(idObj.toString());
                        } catch (NumberFormatException ex) {
                            System.err.println("Invalid officer ID format: " + idObj);
                            continue; // Skip this row
                        }
                    }
                    String name = String.valueOf(model.getValueAt(i, 1));
                    String rank = String.valueOf(model.getValueAt(i, 2));
                    String station = String.valueOf(model.getValueAt(i, 3));
                    
                    PoliceOfficer officer = new PoliceOfficer();
                    officer.setId(id);
                    officer.setName(name);
                    officer.setRank(rank);
                    officer.setStation(station);
                    
                    boolean success = PoliceOfficerDAO.updatePoliceOfficer(officer);
                    if (!success) {
                        allSuccess = false;
                        System.err.println("Failed to update officer ID: " + id);
                    }
                }
                
                if (allSuccess) {
                    JOptionPane.showMessageDialog(this, "All officer updates saved successfully!");
                    // Refresh the data
                    List<PoliceOfficer> officers = PoliceOfficerDAO.getAllPoliceOfficers();
                    model.setRowCount(0);
                    for (PoliceOfficer o : officers) {
                        model.addRow(new Object[]{
                            o.getId(),
                            o.getName(),
                            o.getRank(),
                            o.getStation()
                        });
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Some officer updates failed. Check console for details.", 
                                                 "Update Warning", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating police officers: " + ex.getMessage(), 
                                             "Update Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        panel.add(saveBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createJailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Column names match your model
        String[] cols = {"Jail ID", "Name", "Location", "Capacity"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return col != 0; // allow editing all but ID
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 3) {
                    return Integer.class; // Jail ID and Capacity are integers
                }
                return String.class; // Other columns are strings
            }
        };
        JTable table = new JTable(model);
        
        // Set up a custom cell editor for the capacity column that only accepts numbers
        JTextField capacityField = new JTextField();
        capacityField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!(Character.isDigit(c) || c == java.awt.event.KeyEvent.VK_BACK_SPACE || c == java.awt.event.KeyEvent.VK_DELETE)) {
                    evt.consume(); // Ignore non-digit characters
                }
            }
        });
        table.getColumnModel().getColumn(3).setCellEditor(new javax.swing.DefaultCellEditor(capacityField));
        
        // Set left alignment for ID columns
        javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer); // Jail ID
        
        try {
            // Load data
            System.out.println("Attempting to load jails...");
            List<Jail> jails = JailDAO.getAllJails();
            
            if (jails == null) {
                System.err.println("ERROR: jails list is null");
                JOptionPane.showMessageDialog(this, 
                    "Failed to load jails. See console for details.", 
                    "Data Error", 
                    JOptionPane.ERROR_MESSAGE);
                return panel;
            }
            
            System.out.println("Loaded " + jails.size() + " jails");
            
            for (Jail jail : jails) {
                if (jail == null) {
                    System.err.println("WARNING: Null jail object in list");
                    continue;
                }
                
                try {
                    model.addRow(new Object[]{
                        jail.getJailId(),
                        jail.getName(),
                        jail.getLocation(),
                        jail.getCapacity()
                    });
                    System.out.println("Added jail: " + jail.getName());
                } catch (Exception ex) {
                    System.err.println("Error adding jail to table: " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            System.err.println("Error in createJailPanel: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading jails: " + ex.getMessage(), 
                "Data Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Save button to persist any edits back to DB
        JButton saveBtn = new JButton("Save Jail Changes");
        saveBtn.addActionListener(e -> {
            try {
                // Stop cell editing if any cell is being edited
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }
                
                boolean allSuccess = true;
                
                for (int i = 0; i < model.getRowCount(); i++) {
                Object idObj = model.getValueAt(i, 0);
                int id;
                if (idObj instanceof Integer) {
                    id = (Integer) idObj;
                } else {
                    try {
                        id = Integer.parseInt(idObj.toString());
                    } catch (NumberFormatException ex) {
                        System.err.println("Invalid jail ID format: " + idObj);
                        continue; // Skip this row
                    }
                }
                String name = String.valueOf(model.getValueAt(i, 1));
                String location = String.valueOf(model.getValueAt(i, 2));
                Object capacityObj = model.getValueAt(i, 3);
                int capacity;
                if (capacityObj instanceof Integer) {
                    capacity = (Integer) capacityObj;
                } else {
                    try {
                        String capacityStr = capacityObj.toString().trim();
                        if (capacityStr.isEmpty()) {
                            System.err.println("Empty capacity for jail ID: " + id + ". Using default value.");
                            capacity = 0; // Default value
                        } else {
                            capacity = Integer.parseInt(capacityStr);
                        }
                    } catch (NumberFormatException ex) {
                        System.err.println("Invalid capacity format for jail ID: " + id + ": " + capacityObj);
                        capacity = 0; // Default value
                    }
                }
                
                System.out.println("Updating jail: ID=" + id + 
                                  ", Name=" + name + 
                                  ", Location=" + location + 
                                  ", Capacity=" + capacity);
                
                Jail jail = new Jail();
                jail.setJailId(id);
                jail.setName(name);
                jail.setLocation(location);
                jail.setCapacity(capacity);
                
                boolean success = JailDAO.updateJail(jail);
                if (!success) {
                    allSuccess = false;
                    System.err.println("Failed to update jail ID: " + id);
                }
            }
            
            if (allSuccess) {
                JOptionPane.showMessageDialog(this, "All jail updates saved successfully!");
                
                // Refresh the data to show the updates
                List<Jail> jails = JailDAO.getAllJails();
                model.setRowCount(0);
                for (Jail jail : jails) {
                    model.addRow(new Object[]{
                        jail.getJailId(),
                        jail.getName(),
                        jail.getLocation(),
                        jail.getCapacity()
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "Some jail updates failed. Check console for details.", 
                                             "Update Warning", JOptionPane.WARNING_MESSAGE);
            }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating jails: " + ex.getMessage(), 
                                             "Update Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        panel.add(saveBtn, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createCriminalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Column names match your model
        String[] cols = {"ID", "Name", "Status", "Crime Type", "Arrest Date", "Jail", "Case IDs"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return col == 1 || col == 2; // Only allow editing name and status
            }
        };
        JTable table = new JTable(model);
        
        try {
            // Load data
            System.out.println("Loading criminals...");
            List<Criminal> criminals = CriminalDAO.getAllCriminals();
            
            if (criminals == null) {
                System.err.println("ERROR: criminals list is null");
                JOptionPane.showMessageDialog(this, 
                    "Failed to load criminals. See console for details.", 
                    "Data Error", 
                    JOptionPane.ERROR_MESSAGE);
                return panel;
            }
            
            System.out.println("Loaded " + criminals.size() + " criminals");
            
            for (Criminal c : criminals) {
                if (c == null) {
                    System.err.println("WARNING: Null criminal object in list");
                    continue;
                }
                
                try {
                    model.addRow(new Object[]{
                        c.getId(),
                        c.getName(),
                        c.getStatus(),
                        c.getAddress()
                    });
                } catch (Exception ex) {
                    System.err.println("Error adding criminal to table: " + ex.getMessage());
                }
            }
            
            // Add a note about database limitations
            JLabel noteLabel = new JLabel("<html><b>Note:</b> Only Name and Status can be edited. Other fields are not stored in the database.</html>");
            panel.add(noteLabel, BorderLayout.NORTH);
            
        } catch (Exception ex) {
            System.err.println("Error in createCriminalPanel: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading criminals: " + ex.getMessage(), 
                "Data Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Save button to persist any edits back to DB
        JButton saveBtn = new JButton("Save Criminal Changes");
        saveBtn.addActionListener(e -> {
            try {
                // Stop cell editing if any cell is being edited
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }
                
                boolean allSuccess = true;
                
                for (int i = 0; i < model.getRowCount(); i++) {
                    Object idObj = model.getValueAt(i, 0);
                    int id;
                    if (idObj instanceof Integer) {
                        id = (Integer) idObj;
                    } else {
                        try {
                            id = Integer.parseInt(idObj.toString());
                        } catch (NumberFormatException ex) {
                            System.err.println("Invalid criminal ID format: " + idObj);
                            continue; // Skip this row
                        }
                    }
                    
                    String name = String.valueOf(model.getValueAt(i, 1));
                    String status = String.valueOf(model.getValueAt(i, 2));
                    
                    
                    String address = "";
                    LocalDate arrestDate = null;
                   
                    
                    Criminal criminal = new Criminal();
                    criminal.setId(id);
                    criminal.setName(name);
                    criminal.setStatus(status);
                    criminal.setAddress(address);
                    
                    boolean success = CriminalDAO.updateCriminal(criminal);
                    if (!success) {
                        allSuccess = false;
                        System.err.println("Failed to update criminal ID: " + id);
                    }
                }
                
                if (allSuccess) {
                    JOptionPane.showMessageDialog(this, "All criminal updates saved successfully!");
                    // Refresh the data
                    List<Criminal> criminals = CriminalDAO.getAllCriminals();
                    model.setRowCount(0);
                    for (Criminal c : criminals) {
                        model.addRow(new Object[]{
                            c.getId(),
                            c.getName(),
                            c.getStatus(),
                            c.getAddress(),
                            
                        });
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Some criminal updates failed. Check console for details.", 
                                                 "Update Warning", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating criminals: " + ex.getMessage(), 
                                             "Update Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        // Add button to add new criminals
        JButton addBtn = new JButton("Add New Criminal");
        addBtn.addActionListener(e -> {
            ViewCriminalsFrame criminalsFrame = new ViewCriminalsFrame();
            criminalsFrame.setVisible(true);
            dispose(); // Close this frame
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveBtn);
        buttonPanel.add(addBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PoliceSourcesFrame frame = new PoliceSourcesFrame();
            frame.setVisible(true);
        });
    }
}
