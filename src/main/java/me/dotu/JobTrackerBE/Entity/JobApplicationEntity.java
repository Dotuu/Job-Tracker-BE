package me.dotu.JobTrackerBE.Entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import me.dotu.JobTrackerBE.Model.ApplicationStatus;

@Entity
public class JobApplicationEntity {
  @Id 
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  private String userId; // clerk user id
  private String companyName;
  private String roleTitle;
  private String jobUrl;
  private ApplicationStatus status;
  private LocalDate dateApplied;
  private LocalDate dateRejected; // TODO: have this auto reject once status is changed to rejected
  private String notes;
  private String source; // linkedin, referral, etc
  private int resumeId;
  private LocalTime createdAt;
  private LocalTime updatedAt;


  protected JobApplicationEntity() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getRoleTitle() {
    return roleTitle;
  }

  public void setRoleTitle(String roleTitle) {
    this.roleTitle = roleTitle;
  }

  public String getJobUrl() {
    return jobUrl;
  }

  public void setJobUrl(String jobUrl) {
    this.jobUrl = jobUrl;
  }

  public ApplicationStatus getStatus() {
    return status;
  }

  public void setStatus(ApplicationStatus status) {
    this.status = status;
  }

  public LocalDate getDateApplied() {
    return dateApplied;
  }

  public void setDateApplied(LocalDate dateApplied) {
    this.dateApplied = dateApplied;
  }

  public LocalDate getDateRejected() {
    return dateRejected;
  }

  public void setDateRejected(LocalDate dateRejected) {
    this.dateRejected = dateRejected;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public int getResumeId() {
    return resumeId;
  }

  public void setResumeId(int resumeId) {
    this.resumeId = resumeId;
  }

  public LocalTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
