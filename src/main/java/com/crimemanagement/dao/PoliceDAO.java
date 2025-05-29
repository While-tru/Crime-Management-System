/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.dao;

import com.crimemanagement.model.PoliceOfficer;
import com.crimemanagement.util.DBConnection;
import java.sql.*;
import java.util.*;

public class PoliceDAO {
    public List<PoliceOfficer> getAllPoliceOfficers() {
        List<PoliceOfficer> officers = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT * FROM police_officers";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PoliceOfficer officer = new PoliceOfficer();
                officer.setId(rs.getInt("id"));
                officer.setName(rs.getString("name"));
                officer.setRank(rs.getString("rank"));
                officers.add(officer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return officers;
    }
}