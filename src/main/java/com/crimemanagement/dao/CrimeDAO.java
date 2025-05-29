/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.dao;
import com.crimemanagement.model.crime;
import com.crimemanagement.util.DBConnection;
import java.sql.*;
import java.util.*;

public class CrimeDAO {
    public List<crime> getAllCrimes() {
        List<crime> crimes = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT * FROM crimes";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                crime crime = new crime();
                crime.setId(rs.getInt("id"));
                crime.setType(rs.getString("type"));
                crime.setLocation(rs.getString("location"));
                crime.setDate(rs.getDate("date"));
                crimes.add(crime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return crimes;
    }
}