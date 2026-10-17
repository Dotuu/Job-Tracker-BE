package me.dotu.JobTrackerBE.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.dotu.JobTrackerBE.Entity.JobApplicationEntity;
import me.dotu.JobTrackerBE.Response.JobApplicationResponse;
import me.dotu.JobTrackerBE.Service.JobApplicationService;

@RestController
@RequestMapping("api/applications")
public class JobApplicationController {
  @Autowired 
  public JobApplicationService jobAppService;
  
  @GetMapping(produces = "application/json")
    public ResponseEntity<List<JobApplicationResponse>> getApplications(@AuthenticationPrincipal Jwt jwt) {
      String clerkUserId = jwt.getSubject();
      return ResponseEntity.ok(jobAppService.getApplicationsForUser(clerkUserId));
  }

  @GetMapping(path="/search", produces="application/json")
    public ResponseEntity<List<JobApplicationResponse>> getSearchedApplications(@AuthenticationPrincipal Jwt jwt, @RequestParam String value){
      String clerkUserId = jwt.getSubject();
      return ResponseEntity.ok(jobAppService.getSearchedApplications(clerkUserId, value));
    }
  
  @PostMapping(consumes = "application/json")
  public ResponseEntity<JobApplicationResponse> createApplication(@AuthenticationPrincipal Jwt jwt, @RequestBody JobApplicationEntity jobAppEntity){
    String clerkUserId = jwt.getSubject();
    return jobAppService.createApplication(clerkUserId, jobAppEntity);
  }
  
  @PutMapping("/{id}")
  public ResponseEntity<JobApplicationResponse> updateApplication(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id, @RequestBody JobApplicationEntity jobAppEntity){
    String clerkUserId = jwt.getSubject();
    return jobAppService.updateApplication(clerkUserId, id, jobAppEntity);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteApplication(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id){
    String clerkUserId = jwt.getSubject();
    return jobAppService.deleteApplication(clerkUserId, id);
  }
}
