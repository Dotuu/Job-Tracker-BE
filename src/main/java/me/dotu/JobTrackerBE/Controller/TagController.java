package me.dotu.JobTrackerBE.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import me.dotu.JobTrackerBE.Response.TagResponse;
import me.dotu.JobTrackerBE.Service.TagService;

@RestController
@RequestMapping("/api/tags")
public class TagController {
  @Autowired
  private TagService tagService;

  public record CreateTagRequest(String name, String colorHex, String scope) {}
  public record UpdateTagRequest(String name, String colorHex) {}

  @GetMapping(produces = "application/json")
  public List<TagResponse> getTag(@AuthenticationPrincipal Jwt jwt){
    String clerkUserId = jwt.getSubject();
    return tagService.getTagForUser(clerkUserId);
  }

  @PostMapping(consumes = "application/json")
  public ResponseEntity<TagResponse> createTag(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateTagRequest request){
    String clerkUserId = jwt.getSubject();
    return tagService.createTag(clerkUserId, request.name(), request.colorHex(), request.scope());
  }

  @PatchMapping("/{id}")
  public ResponseEntity<TagResponse> updateTag(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id, @RequestBody UpdateTagRequest request){
    String clerkUserId = jwt.getSubject();
    return tagService.updateTag(clerkUserId, id, request.name(), request.colorHex());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTag(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id){
    String clerkUserId = jwt.getSubject();
    return tagService.deleteTag(clerkUserId, id);
  }
}
