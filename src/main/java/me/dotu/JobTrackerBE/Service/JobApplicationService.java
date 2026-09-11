package me.dotu.JobTrackerBE.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import me.dotu.JobTrackerBE.Entity.JobApplicationEntity;
import me.dotu.JobTrackerBE.Repository.JobApplicationRepository;

public class JobApplicationService {

  @Autowired 
  private JobApplicationRepository applicationRepo;

  protected ResponseEntity<?> getApplications(){
    List<JobApplicationEntity> applications = applicationRepo.findAll();
    return new ResponseEntity<>(applications, HttpStatus.OK);
  }
}
