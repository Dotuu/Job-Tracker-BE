package me.dotu.JobTrackerBE.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import me.dotu.JobTrackerBE.Response.ResumeResponse;
import me.dotu.JobTrackerBE.Service.ResumeService;

@RestController
@RequestMapping("api/resumes")
public class ResumeController {
  @Autowired
  private ResumeService resumeService;

  public record RenameResumeRequest(String fileName) {}

  @GetMapping(produces = "application/json")
  public ResponseEntity<List<ResumeResponse>> getResumes(@AuthenticationPrincipal Jwt jwt) {
    String clerkUserId = jwt.getSubject();
    return ResponseEntity.ok(resumeService.getResumesForUser(clerkUserId));
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
  public ResponseEntity<ResumeResponse> uploadResume(@AuthenticationPrincipal Jwt jwt, @RequestParam("file") MultipartFile file,
      @RequestParam(value = "title", required = false) String title) {
    String clerkUserId = jwt.getSubject();
    return resumeService.uploadResume(clerkUserId, file, title);
  }

  @GetMapping("/{id}/download")
  public ResponseEntity<Resource> downloadResume(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
    String clerkUserId = jwt.getSubject();
    return resumeService.downloadResume(clerkUserId, id);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ResumeResponse> renameResume(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id, @RequestBody RenameResumeRequest request) {
    String clerkUserId = jwt.getSubject();
    return resumeService.renameResume(clerkUserId, id, request.fileName());
  }

  @PatchMapping("/{id}/default")
  public ResponseEntity<ResumeResponse> setDefaultResume(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
    String clerkUserId = jwt.getSubject();
    return resumeService.setDefaultResume(clerkUserId, id);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteResume(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
    String clerkUserId = jwt.getSubject();
    return resumeService.deleteResume(clerkUserId, id);
  }
}
