package me.dotu.JobTrackerBE.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import me.dotu.JobTrackerBE.Entity.JobApplicationEntity;
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

  public ResponseEntity<JobApplicationResponse> createApplicationForUser(String clerkUserId, JobApplicationEntity jobAppEntity){
    jobAppEntity.setUserId(clerkUserId);
    JobApplicationEntity saved = applicationRepo.save(jobAppEntity);
    return ResponseEntity.status(HttpStatus.CREATED).body(JobApplicationResponse.from(saved));
  }

  public ResponseEntity<JobApplicationResponse> updateApplicationForUser(String clerkUserId, JobApplicationEntity jobAppEntity){
    jobAppEntity.setUserId(clerkUserId);
    JobApplicationEntity saved = applicationRepo.save(jobAppEntity);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(JobApplicationResponse.from(saved));
  }

  public ResponseEntity<JobApplicationResponse> deleteApplicationForUser(String clerkUserId, JobApplicationEntity jobAppEntity){
    jobAppEntity.setUserId(clerkUserId);
    applicationRepo.delete(jobAppEntity);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(JobApplicationResponse.from(jobAppEntity));
  }
}
