package me.dotu.JobTrackerBE.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import me.dotu.JobTrackerBE.Entity.JobApplicationEntity;
import me.dotu.JobTrackerBE.Model.ApplicationStatus;
import me.dotu.JobTrackerBE.Repository.JobApplicationRepository;
import me.dotu.JobTrackerBE.Response.JobApplicationResponse;

@Service
public class JobApplicationService {

  @Autowired 
  private JobApplicationRepository applicationRepo;

  public List<JobApplicationResponse> getApplicationsForUser(String clerkUserId){
    return applicationRepo.findByUserId(clerkUserId)
    .stream()
    .map(JobApplicationResponse::from)
    .toList();
  }

  public ResponseEntity<JobApplicationResponse> createApplication(String clerkUserId, JobApplicationEntity jobAppEntity){
    jobAppEntity.setUserId(clerkUserId);
    JobApplicationEntity saved = applicationRepo.save(jobAppEntity);
    return ResponseEntity.status(HttpStatus.CREATED).body(JobApplicationResponse.from(saved));
  }

  public ResponseEntity<JobApplicationResponse> updateApplication(String clerkUserId, Long applicationId, JobApplicationEntity changes){
    Optional<JobApplicationEntity> jobAppEntityO = applicationRepo.findByIdAndUserId(applicationId, clerkUserId);

    if (jobAppEntityO.isEmpty()){
      return ResponseEntity.notFound().build();
    }

    JobApplicationEntity jobAppEntity = jobAppEntityO.get();
    jobAppEntity.setCompanyName(changes.getCompanyName());
    jobAppEntity.setRoleTitle(changes.getRoleTitle());
    jobAppEntity.setJobUrl(changes.getJobUrl());
    jobAppEntity.setDateApplied(changes.getDateApplied());
    jobAppEntity.setNotes(changes.getNotes());
    jobAppEntity.setSource(changes.getSource());

    ApplicationStatus newStatus = changes.getStatus();
    if (newStatus != null){
      if (newStatus == ApplicationStatus.REJECTED && jobAppEntity.getDateRejected() == null){
        jobAppEntity.setDateRejected(LocalDate.now());
      }
      jobAppEntity.setStatus(newStatus);
    }

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
