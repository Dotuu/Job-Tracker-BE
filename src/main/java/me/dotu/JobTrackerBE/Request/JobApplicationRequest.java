package me.dotu.JobTrackerBE.Request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import me.dotu.JobTrackerBE.Model.ApplicationStatus;
import me.dotu.JobTrackerBE.Model.LocationType;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JobApplicationRequest(
  @NotBlank @Size(max = 255) String companyName,
  @NotBlank @Size(max = 255) String roleTitle,
  @Size(max = 2048)
  @Pattern(regexp = "^$|^https?://\\S+$", flags = Pattern.Flag.CASE_INSENSITIVE,
    message = "must be an http(s) URL")
  String jobUrl,
  @NotNull LocalDate dateApplied,
  @Size(max = 100) String source,
  ApplicationStatus status,
  @Size(max = 255) String location,
  LocationType locationType
) {
  public static final int MAX_NOTES_LENGTH = 10_000;
}
