package me.dotu.JobTrackerBE.Response;

import java.time.LocalDateTime;

import me.dotu.JobTrackerBE.Entity.ResumeEntity;

public record ResumeResponse(
    Long id,
    String fileName,
    String contentType,
    Long sizeBytes,
    boolean isDefault,
    LocalDateTime uploadedAt
) {
  public static ResumeResponse from(ResumeEntity entity) {
    return new ResumeResponse(
        entity.getId(),
        entity.getFileName(),
        entity.getContentType(),
        entity.getSizeBytes(),
        entity.isDefault(),
        entity.getUploadedAt());
  }
}
