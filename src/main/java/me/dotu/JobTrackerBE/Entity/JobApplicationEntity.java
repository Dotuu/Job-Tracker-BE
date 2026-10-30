package me.dotu.JobTrackerBE.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import me.dotu.JobTrackerBE.Model.ApplicationStatus;
import me.dotu.JobTrackerBE.Model.LocationType;

@Entity
@Table(name = "job_applications")
public class JobApplicationEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "company_name", nullable = false)
  private String companyName;

  @Column(name = "role_title", nullable = false)
  private String roleTitle;

  @Column(name = "job_url", length = 2048)
  private String jobUrl;

  @Column(name = "location", nullable = true)
  private String location;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private LocationType locationType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private ApplicationStatus status;

  @Column(name = "date_applied", nullable = false)
  private LocalDate dateApplied;

  @Column(name = "date_rejected") 
  private LocalDate dateRejected;

  @Column(columnDefinition = "TEXT")
  private String notes;

  @Column(length = 100)
  private String source;

  @Column(name = "resume_id")
  private Long resumeId;

  @JsonIgnore
  @ManyToMany
  @JoinTable(
    name = "job_application_tags",
    joinColumns = @JoinColumn(name = "job_application_id"),
    inverseJoinColumns = @JoinColumn(name = "tag_id")
  )
  private Set<TagEntity> tags = new HashSet<>();

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

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

  public Long getResumeId() {
    return resumeId;
  }

  public void setResumeId(Long resumeId) {
    this.resumeId = resumeId;
  }

  public Set<TagEntity> getTags() {
    return tags;
  }

  public void setTags(Set<TagEntity> tags) {
    this.tags = tags;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }
}
