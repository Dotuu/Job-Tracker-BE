package me.dotu.JobTrackerBE.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.dotu.JobTrackerBE.Response.StatusHistoryResponse;
import me.dotu.JobTrackerBE.Service.StatusHistoryService;

@RestController
@RequestMapping("/api/stats")
public class StatusHistoryContoller {

  @Autowired
  StatusHistoryService statusHistoryService;

  @GetMapping(produces = "application/json")
  public ResponseEntity<List<StatusHistoryResponse>> getStatusHistory(@AuthenticationPrincipal Jwt jwt){
    String clerkUserId = jwt.getSubject();
    return ResponseEntity.ok(statusHistoryService.getStatusHistory(clerkUserId));
  }
}
