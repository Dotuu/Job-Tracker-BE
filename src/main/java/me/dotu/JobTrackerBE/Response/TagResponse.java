package me.dotu.JobTrackerBE.Response;

import java.time.LocalDateTime;

import me.dotu.JobTrackerBE.Entity.TagEntity;

  public record TagResponse(
    Long id,
    String name,
    String colorHex,
    String scope,
    LocalDateTime createdAt
) {
  public static TagResponse from(TagEntity entity) {
    return new TagResponse(
        entity.getId(),
        entity.getName(),
        entity.getColorHex(),
        entity.getScope(),
        entity.getCreatedAt());
  }
}