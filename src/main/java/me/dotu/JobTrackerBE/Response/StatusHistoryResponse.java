package me.dotu.JobTrackerBE.Response;

import java.time.LocalDateTime;

import me.dotu.JobTrackerBE.Entity.StatusHistoryEntity;
import me.dotu.JobTrackerBE.Model.ApplicationStatus;

public record StatusHistoryResponse (
  Long id,
  Long jobApplicationId,
  ApplicationStatus status,
  LocalDateTime changedAt
) {
  public static StatusHistoryResponse from(StatusHistoryEntity entity){
    return new StatusHistoryResponse(
      entity.getId(), 
      entity.getJobApplicationId(), 
      entity.getStatus(), 
      entity.getChangedAt());
  }
}