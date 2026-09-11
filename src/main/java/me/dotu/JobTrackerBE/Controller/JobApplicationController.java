package me.dotu.JobTrackerBE.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.dotu.JobTrackerBE.Entity.JobApplicationEntity;

@RestController
@RequestMapping("api/applications")
public class JobApplicationController {
  @Autowired 
  public JobApplicationEntity jobApp;

  @GetMapping("api/applications")
  public void getApplications(@AuthenticationPrincipal Jwt jwt){
    String clerkUserId = jwt.getSubject();
  }  
}
