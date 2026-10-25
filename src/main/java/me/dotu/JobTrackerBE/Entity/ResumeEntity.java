package me.dotu.JobTrackerBE.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "resumes")
public class ResumeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String userId;

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false, unique = true, length = 100)
  private String storagePath;

  @Column(nullable = false, length = 100)
  private String contentType;

  @Column(nullable = false)
  private Long sizeBytes;

  @Column(columnDefinition = "CHAR(64)")
  private String sha256;

  @Column(nullable = false)
  private boolean isDefault;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime uploadedAt;

  public Long getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getStoragePath() {
    return storagePath;
  }

  public void setStoragePath(String storagePath) {
    this.storagePath = storagePath;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public Long getSizeBytes() {
    return sizeBytes;
  }

  public void setSizeBytes(Long sizeBytes) {
    this.sizeBytes = sizeBytes;
  }

  public String getSha256() {
    return sha256;
  }

  public void setSha256(String sha256) {
    this.sha256 = sha256;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public void setIsDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

  public LocalDateTime getUploadedAt() {
    return uploadedAt;
  }
}
