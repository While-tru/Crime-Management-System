/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.crimemanagement.model;

import java.time.LocalDate;

public class ForensicReport {
    private int reportId;
    private int evidenceId;
    private String findings;
    private String preparedBy;
    private LocalDate date;

    // Constructors
    public ForensicReport() {}

    public ForensicReport(int reportId, int evidenceId, String findings, String preparedBy, LocalDate date) {
        this.reportId = reportId;
        this.evidenceId = evidenceId;
        this.findings = findings;
        this.preparedBy = preparedBy;
        this.date = date;
    }

    // Getters and Setters
    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(int evidenceId) {
        this.evidenceId = evidenceId;
    }

    public String getFindings() {
        return findings;
    }

    public void setFindings(String findings) {
        this.findings = findings;
    }

    public String getPreparedBy() {
        return preparedBy;
    }

    public void setPreparedBy(String preparedBy) {
        this.preparedBy = preparedBy;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
