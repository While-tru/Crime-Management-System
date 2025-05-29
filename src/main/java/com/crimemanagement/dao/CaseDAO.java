package com.crimemanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.crimemanagement.model.Case;
import com.crimemanagement.util.DBConnection;

public class CaseDAO {
    
    public static boolean createCase(Case caseObj) {
        // Updated to match the actual database schema
        String insertCaseQuery = "INSERT INTO casetable (status, date_started) VALUES (?, ?)";
        String insertCrimeQuery = "INSERT INTO crime (type, location, case_id) VALUES (?, ?, ?)";
        
        Connection con = null;
        PreparedStatement caseStmt = null;
        PreparedStatement crimeStmt = null;
        ResultSet generatedKeys = null;
        boolean success = false;
        
        try {
            con = DBConnection.getConnection();
            if (con == null) {
                System.err.println("Failed to get database connection in createCase");
                return false;
            }
            
            con.setAutoCommit(false); // Begin transaction
            
            // Insert into CaseTable
            caseStmt = con.prepareStatement(insertCaseQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            
            String status = caseObj.getStatus() != null ? caseObj.getStatus() : "Open";
            java.sql.Date dateStarted = caseObj.getDateStarted() != null ? 
                            java.sql.Date.valueOf(caseObj.getDateStarted()) : 
                            java.sql.Date.valueOf(java.time.LocalDate.now());
            
            caseStmt.setString(1, status);
            caseStmt.setDate(2, dateStarted);
            
            System.out.println("Executing SQL: " + insertCaseQuery);
            System.out.println("Parameters: [" + status + ", " + dateStarted + "]");
            
            int caseRows = caseStmt.executeUpdate();
            
            if (caseRows > 0) {
                // Get the generated case ID
                generatedKeys = caseStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int caseId = generatedKeys.getInt(1);
                    
                    // Insert into Crime table
                    crimeStmt = con.prepareStatement(insertCrimeQuery);
                    crimeStmt.setString(1, caseObj.getCrimeType() != null ? caseObj.getCrimeType() : "Unknown");
                    crimeStmt.setString(2, caseObj.getCrimeLocation() != null ? caseObj.getCrimeLocation() : "Unknown");
                    crimeStmt.setInt(3, caseId);
                    
                    int crimeRows = crimeStmt.executeUpdate();
                    
                    if (crimeRows > 0) {
                        con.commit();
                        System.out.println("Case created successfully with ID: " + caseId);
                        caseObj.setId(caseId); // Update the case object with the new ID
                        success = true;
                    } else {
                        con.rollback();
                        System.err.println("Failed to insert crime record. Transaction rolled back.");
                    }
                } else {
                    con.rollback();
                    System.err.println("Failed to get generated case ID. Transaction rolled back.");
                }
            } else {
                con.rollback();
                System.err.println("Failed to insert case record. Transaction rolled back.");
            }
            
        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback(); // Rollback on exception
                    System.err.println("Transaction rolled back due to exception: " + e.getMessage());
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
                rollbackEx.printStackTrace();
            }
            System.err.println("Error in createCase: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (caseStmt != null) caseStmt.close();
                if (crimeStmt != null) crimeStmt.close();
                if (con != null) {
                    con.setAutoCommit(true); // Reset autocommit
                    con.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return success;
    }

    public static List<Case> getAllCases() {
        List<Case> cases = new ArrayList<>();
        String query = "SELECT case_id, status, date_started FROM casetable";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con != null ? con.prepareStatement(query) : null;
             ResultSet rs = stmt != null ? stmt.executeQuery() : null) {

            if (con == null || stmt == null || rs == null) return cases;

            while (rs.next()) {
                Case c = new Case();
                int caseId = rs.getInt("case_id");
                c.setId(caseId);
                c.setTitle("Case #" + caseId); // Default title based on ID
                c.setDescription(""); // Empty description
                c.setStatus(rs.getString("status"));
                
                // Get date_started if available
                java.sql.Date dateStarted = rs.getDate("date_started");
                if (dateStarted != null) {
                    c.setDateStarted(dateStarted.toLocalDate());
                }
                
                cases.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cases;
    }

    public static void updateCase(int id, String title, String description, String status) {
        // Updated to match the actual database schema
        String sql = "UPDATE casetable SET status = ? WHERE case_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) return;

            con.setAutoCommit(false);

            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, status);
                stmt.setInt(2, id);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    con.commit();
                    System.out.println("Case updated successfully and committed.");
                } else {
                    con.rollback();
                    System.out.println("No case found with the given ID. Transaction rolled back.");
                }
            } catch (SQLException e) {
                con.rollback();
                System.out.println("Transaction rolled back due to error.");
                e.printStackTrace();
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Case> getCasesWithCrime() {
        List<Case> caseList = new ArrayList<>();
        String sql = "SELECT ct.case_id, ct.status, ct.date_started, cr.type, cr.location " +
                     "FROM casetable ct LEFT JOIN crime cr ON ct.case_id = cr.case_id";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                System.err.println("Database connection is null in getCasesWithCrime");
                return caseList;
            }
            
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            System.out.println("Executing query: " + sql);

            while (rs.next()) {
                Case caseObj = new Case();
                int caseId = rs.getInt("case_id");
                String status = rs.getString("status");
                java.sql.Date dateStarted = rs.getDate("date_started");
                String type = rs.getString("type");
                String location = rs.getString("location");
                
                caseObj.setId(caseId);
                caseObj.setTitle("Case #" + caseId); // Default title based on ID
                caseObj.setDescription(""); // Empty description
                caseObj.setStatus(status != null ? status : "Pending");
                
                if (dateStarted != null) {
                    caseObj.setDateStarted(dateStarted.toLocalDate());
                }
                
                caseObj.setCrimeType(type != null ? type : "Unknown");
                caseObj.setCrimeLocation(location != null ? location : "Unknown");
                
                caseList.add(caseObj);
                
                System.out.println("Loaded case: ID=" + caseId + 
                                  ", Status=" + status + 
                                  ", Date Started=" + dateStarted + 
                                  ", Crime Type=" + type + 
                                  ", Location=" + location);
            }
            
            System.out.println("Total cases loaded: " + caseList.size());

        } catch (SQLException e) {
            System.err.println("Error in getCasesWithCrime: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return caseList;
    }

    public static boolean deleteCase(int caseId) {
        String deleteCrimeQuery = "DELETE FROM crime WHERE case_id = ?";
        String deleteFIRQuery = "DELETE FROM fir WHERE case_id = ?";
        String deleteCaseQuery = "DELETE FROM casetable WHERE case_id = ?";
        
        Connection con = null;
        PreparedStatement crimeStmt = null;
        PreparedStatement firStmt = null;
        PreparedStatement caseStmt = null;
        boolean success = false;
        
        try {
            con = DBConnection.getConnection();
            if (con == null) {
                System.err.println("Failed to get database connection in deleteCase");
                return false;
            }
            
            con.setAutoCommit(false); // Begin transaction
            
            // First, delete related records in Crime table
            crimeStmt = con.prepareStatement(deleteCrimeQuery);
            crimeStmt.setInt(1, caseId);
            crimeStmt.executeUpdate();
            
            // Next, delete related records in FIR table
            firStmt = con.prepareStatement(deleteFIRQuery);
            firStmt.setInt(1, caseId);
            firStmt.executeUpdate();
            
            // Finally, delete the case record
            caseStmt = con.prepareStatement(deleteCaseQuery);
            caseStmt.setInt(1, caseId);
            int caseRows = caseStmt.executeUpdate();
            
            if (caseRows > 0) {
                con.commit();
                System.out.println("Case deleted successfully: ID=" + caseId);
                success = true;
            } else {
                con.rollback();
                System.err.println("No case found with ID=" + caseId + ". Transaction rolled back.");
            }
            
        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback(); // Rollback on exception
                    System.err.println("Transaction rolled back due to exception: " + e.getMessage());
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
                rollbackEx.printStackTrace();
            }
            System.err.println("Error in deleteCase: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (crimeStmt != null) crimeStmt.close();
                if (firStmt != null) firStmt.close();
                if (caseStmt != null) caseStmt.close();
                if (con != null) {
                    con.setAutoCommit(true); // Reset autocommit
                    con.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return success;
    }
    
    public static boolean updateCase(Case c) {
        // We need to update both the CaseTable and Crime tables
        // Updated to match the actual database schema
        String updateCaseQuery = "UPDATE casetable SET status = ?, date_started = ? WHERE case_id = ?";
        String updateCrimeQuery = "UPDATE crime SET type = ?, location = ? WHERE case_id = ?";
        String insertCrimeQuery = "INSERT INTO crime (type, location, case_id) VALUES (?, ?, ?)";
        
        Connection con = null;
        PreparedStatement caseStmt = null;
        PreparedStatement crimeStmt = null;
        PreparedStatement checkCrimeStmt = null;
        ResultSet rs = null;
        boolean success = false;
        
        try {
            con = DBConnection.getConnection();
            if (con == null) {
                System.err.println("Failed to get database connection in updateCase");
                return false;
            }
            
            con.setAutoCommit(false); // Begin transaction
            
            // Update CaseTable
            caseStmt = con.prepareStatement(updateCaseQuery);
            
            String status = c.getStatus() != null ? c.getStatus() : "Pending";
            java.sql.Date dateStarted = c.getDateStarted() != null ? 
                           java.sql.Date.valueOf(c.getDateStarted()) : 
                           java.sql.Date.valueOf(java.time.LocalDate.now());
            int id = c.getId();
            
            caseStmt.setString(1, status);
            caseStmt.setDate(2, dateStarted);
            caseStmt.setInt(3, id);
            
            System.out.println("Executing SQL: " + updateCaseQuery);
            System.out.println("Parameters: [" + status + ", " + dateStarted + ", " + id + "]");
            
            int caseRows = caseStmt.executeUpdate();
            System.out.println("Case update affected " + caseRows + " rows");
            
            // Check if a crime record exists for this case
            checkCrimeStmt = con.prepareStatement("SELECT COUNT(*) FROM crime WHERE case_id = ?");
            checkCrimeStmt.setInt(1, c.getId());
            rs = checkCrimeStmt.executeQuery();
            
            boolean crimeExists = false;
            if (rs.next()) {
                crimeExists = rs.getInt(1) > 0;
            }
            
            int crimeRows = 0;
            
            if (crimeExists) {
                // Update existing Crime record
                crimeStmt = con.prepareStatement(updateCrimeQuery);
                crimeStmt.setString(1, c.getCrimeType() != null ? c.getCrimeType() : "Unknown");
                crimeStmt.setString(2, c.getCrimeLocation() != null ? c.getCrimeLocation() : "Unknown");
                crimeStmt.setInt(3, c.getId());
                
                crimeRows = crimeStmt.executeUpdate();
                System.out.println("Crime update affected " + crimeRows + " rows");
            } else {
                // Insert new Crime record
                crimeStmt = con.prepareStatement(insertCrimeQuery);
                crimeStmt.setString(1, c.getCrimeType() != null ? c.getCrimeType() : "Unknown");
                crimeStmt.setString(2, c.getCrimeLocation() != null ? c.getCrimeLocation() : "Unknown");
                crimeStmt.setInt(3, c.getId());
                
                crimeRows = crimeStmt.executeUpdate();
                System.out.println("Crime insert affected " + crimeRows + " rows");
            }
            
            // Commit if case update was successful (crime might not exist yet)
            if (caseRows > 0) {
                con.commit();
                System.out.println("Case and Crime updated successfully and committed.");
                success = true;
            } else {
                con.rollback();
                System.err.println("Case update failed. Transaction rolled back.");
            }
            
        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback(); // Rollback on exception
                    System.err.println("Transaction rolled back due to exception: " + e.getMessage());
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
                rollbackEx.printStackTrace();
            }
            System.err.println("Error in updateCase: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (checkCrimeStmt != null) checkCrimeStmt.close();
                if (caseStmt != null) caseStmt.close();
                if (crimeStmt != null) crimeStmt.close();
                if (con != null) {
                    con.setAutoCommit(true); // Reset autocommit
                    con.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return success;
    }
}
