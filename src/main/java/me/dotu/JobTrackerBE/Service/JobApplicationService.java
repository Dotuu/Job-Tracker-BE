package me.dotu.JobTrackerBE.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import me.dotu.JobTrackerBE.Entity.JobApplicationEntity;
import me.dotu.JobTrackerBE.Entity.TagEntity;
import me.dotu.JobTrackerBE.Model.ApplicationStatus;
import me.dotu.JobTrackerBE.Model.LocationType;
import me.dotu.JobTrackerBE.Repository.JobApplicationRepository;
import me.dotu.JobTrackerBE.Repository.ResumeRepository;
import me.dotu.JobTrackerBE.Repository.TagRepository;
import me.dotu.JobTrackerBE.Response.JobApplicationResponse;

@Service
public class JobApplicationService {

  @Autowired
  private JobApplicationRepository applicationRepo;

  @Autowired
  private ResumeRepository resumeRepo;

  @Autowired
  private TagRepository tagRepo;

  public List<JobApplicationResponse> getApplicationsForUser(String clerkUserId){
    return applicationRepo.findByUserId(clerkUserId)
    .stream()
    .map(JobApplicationResponse::from)
    .toList();
  }

  public List<JobApplicationResponse> getSearchedApplications(String clerkUserId, String searchParams){
    return searchParams.startsWith("#") ?
      applicationRepo.searchTagsByUserId(clerkUserId, searchParams.replaceFirst("#", "")).stream().map(JobApplicationResponse::from).toList() :
        applicationRepo.searchByUserId(clerkUserId, searchParams).stream().map(JobApplicationResponse::from).toList();
  }

  private JobApplicationEntity checkEnums(JobApplicationEntity jobAppEntity){
  if (jobAppEntity.getStatus() == null) {
    jobAppEntity.setStatus(ApplicationStatus.APPLIED);
  }
  if (jobAppEntity.getLocationType() == null) {
    jobAppEntity.setLocationType(LocationType.REMOTE);
  }
  return jobAppEntity;
}

  public ResponseEntity<JobApplicationResponse> createApplication(String clerkUserId, JobApplicationEntity jobAppEntity){
    jobAppEntity.setId(null);
    jobAppEntity.setResumeId(null);
    jobAppEntity.setUserId(clerkUserId);

    jobAppEntity = checkEnums(jobAppEntity);

    JobApplicationEntity saved = applicationRepo.save(jobAppEntity);
    return ResponseEntity.status(HttpStatus.CREATED).body(JobApplicationResponse.from(saved));
  }

  public ResponseEntity<JobApplicationResponse> updateNotes(String clerkUserId, Long applicationId, String notes){
    Optional<JobApplicationEntity> jobAppEntityO = applicationRepo.findByIdAndUserId(applicationId, clerkUserId);

    if (!jobAppEntityO.isPresent()){
      return ResponseEntity.notFound().build();
    }

    JobApplicationEntity jobAppEntity = jobAppEntityO.get();
    jobAppEntity.setNotes(notes);

    JobApplicationEntity saved = applicationRepo.save(jobAppEntity);
    return ResponseEntity.ok(JobApplicationResponse.from(saved));
  }

  public ResponseEntity<JobApplicationResponse> updateApplication(String clerkUserId, Long applicationId, JobApplicationEntity changes){
    Optional<JobApplicationEntity> jobAppEntityO = applicationRepo.findByIdAndUserId(applicationId, clerkUserId);

    if (!jobAppEntityO.isPresent()){
      return ResponseEntity.notFound().build();
    }

    JobApplicationEntity jobAppEntity = checkEnums(jobAppEntityO.get());
    jobAppEntity.setCompanyName(changes.getCompanyName());
    jobAppEntity.setRoleTitle(changes.getRoleTitle());
    jobAppEntity.setJobUrl(changes.getJobUrl());
    jobAppEntity.setDateApplied(changes.getDateApplied());
    jobAppEntity.setNotes(changes.getNotes());
    jobAppEntity.setSource(changes.getSource());
    jobAppEntity.setLocation(changes.getLocation());
    jobAppEntity.setLocationType(changes.getLocationType());

    ApplicationStatus newStatus = changes.getStatus();

    if (newStatus == ApplicationStatus.REJECTED && jobAppEntity.getDateRejected() == null){
      jobAppEntity.setDateRejected(LocalDate.now());
    }
    jobAppEntity.setStatus(newStatus);

    JobApplicationEntity saved = applicationRepo.save(jobAppEntity);
    return ResponseEntity.ok(JobApplicationResponse.from(saved));
  }

  public ResponseEntity<JobApplicationResponse> updateResume(String clerkUserId, Long applicationId, Long resumeId){
    Optional<JobApplicationEntity> jobAppEntityO = applicationRepo.findByIdAndUserId(applicationId, clerkUserId);

    if (jobAppEntityO.isEmpty()){
      return ResponseEntity.notFound().build();
    }

    if (resumeId != null && !resumeRepo.existsByIdAndUserId(resumeId, clerkUserId)){
      return ResponseEntity.notFound().build();
    }

    JobApplicationEntity jobAppEntity = jobAppEntityO.get();
    jobAppEntity.setResumeId(resumeId);

    JobApplicationEntity saved = applicationRepo.save(jobAppEntity);
    return ResponseEntity.ok(JobApplicationResponse.from(saved));
  }

  public ResponseEntity<JobApplicationResponse> updateTags(String clerkUserId, Long applicationId, List<Long> tagIds){
    Optional<JobApplicationEntity> jobAppEntityO = applicationRepo.findByIdAndUserId(applicationId, clerkUserId);

    if (jobAppEntityO.isEmpty()){
      return ResponseEntity.notFound().build();
    }

    Set<Long> requestedIds = tagIds == null ? Set.of() : new HashSet<>(tagIds);
    List<TagEntity> tags = requestedIds.isEmpty()
      ? List.of()
      : tagRepo.findAllByIdInAndUserId(requestedIds, clerkUserId);

    if (tags.size() != requestedIds.size()){
      return ResponseEntity.notFound().build();
    }

    JobApplicationEntity jobAppEntity = jobAppEntityO.get();
    jobAppEntity.getTags().clear();
    jobAppEntity.getTags().addAll(tags);

    JobApplicationEntity saved = applicationRepo.save(jobAppEntity);
    return ResponseEntity.ok(JobApplicationResponse.from(saved));
  }

  public ResponseEntity<Void> deleteApplication(String clerkUserId, Long applicationId){
    Optional<JobApplicationEntity> jobAppEntityO = applicationRepo.findByIdAndUserId(applicationId, clerkUserId);

    if (jobAppEntityO.isEmpty()){
      return ResponseEntity.notFound().build();
    }

    applicationRepo.delete(jobAppEntityO.get());
    return ResponseEntity.noContent().build();
  }
}
