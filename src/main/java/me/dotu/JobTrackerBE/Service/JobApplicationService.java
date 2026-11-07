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

import jakarta.transaction.Transactional;
import me.dotu.JobTrackerBE.Entity.JobApplicationEntity;
import me.dotu.JobTrackerBE.Entity.StatusHistoryEntity;
import me.dotu.JobTrackerBE.Entity.TagEntity;
import me.dotu.JobTrackerBE.Model.ApplicationStatus;
import me.dotu.JobTrackerBE.Model.LocationType;
import me.dotu.JobTrackerBE.Repository.JobApplicationRepository;
import me.dotu.JobTrackerBE.Repository.ResumeRepository;
import me.dotu.JobTrackerBE.Repository.StatusHistoryRepository;
import me.dotu.JobTrackerBE.Repository.TagRepository;
import me.dotu.JobTrackerBE.Request.JobApplicationRequest;
import me.dotu.JobTrackerBE.Response.JobApplicationResponse;

@Service
public class JobApplicationService {

  @Autowired
  private JobApplicationRepository applicationRepo;

  @Autowired
  private ResumeRepository resumeRepo;

  @Autowired
  private TagRepository tagRepo;

  @Autowired
  private StatusHistoryRepository statusHistoryRepository;

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

  private static ApplicationStatus statusOrDefault(ApplicationStatus status){
    return status == null ? ApplicationStatus.APPLIED : status;
  }

  private static LocationType locationTypeOrDefault(LocationType locationType){
    return locationType == null ? LocationType.REMOTE : locationType;
  }

  private static String blankToNull(String value){
    return value == null || value.isBlank() ? null : value.trim();
  }

  private static void applyRequest(JobApplicationEntity entity, JobApplicationRequest request){
    entity.setCompanyName(request.companyName().trim());
    entity.setRoleTitle(request.roleTitle().trim());
    entity.setJobUrl(blankToNull(request.jobUrl()));
    entity.setDateApplied(request.dateApplied());
    entity.setSource(blankToNull(request.source()));
    entity.setLocation(blankToNull(request.location()));
    entity.setLocationType(locationTypeOrDefault(request.locationType()));
  }

  @Transactional
  public ResponseEntity<JobApplicationResponse> createApplication(String clerkUserId, JobApplicationRequest request){
    JobApplicationEntity jobAppEntity = new JobApplicationEntity();
    jobAppEntity.setUserId(clerkUserId);
    applyRequest(jobAppEntity, request);

    ApplicationStatus status = statusOrDefault(request.status());
    jobAppEntity.setStatus(status);
    if (status == ApplicationStatus.REJECTED){
      jobAppEntity.setDateRejected(LocalDate.now());
    }

    JobApplicationEntity saved = applicationRepo.save(jobAppEntity);
    statusHistoryRepository.save(new StatusHistoryEntity(clerkUserId, saved.getId(), saved.getStatus()));

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

  @Transactional 
  public ResponseEntity<JobApplicationResponse> updateApplication(String clerkUserId, Long applicationId, JobApplicationRequest request){
    Optional<JobApplicationEntity> jobAppEntityO = applicationRepo.findByIdAndUserId(applicationId, clerkUserId);

    if (!jobAppEntityO.isPresent()){
      return ResponseEntity.notFound().build();
    }

    JobApplicationEntity jobAppEntity = jobAppEntityO.get();
    // Notes are intentionally not touched here; they have their own PATCH /notes endpoint.
    applyRequest(jobAppEntity, request);

    ApplicationStatus oldStatus = jobAppEntity.getStatus();
    ApplicationStatus newStatus = statusOrDefault(request.status());

    if (newStatus == ApplicationStatus.REJECTED && jobAppEntity.getDateRejected() == null){
      jobAppEntity.setDateRejected(LocalDate.now());
    }
    jobAppEntity.setStatus(newStatus);

    JobApplicationEntity saved = applicationRepo.save(jobAppEntity);

    if (newStatus != oldStatus){
      statusHistoryRepository.save(new StatusHistoryEntity(clerkUserId, saved.getId(), newStatus));
    }

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
