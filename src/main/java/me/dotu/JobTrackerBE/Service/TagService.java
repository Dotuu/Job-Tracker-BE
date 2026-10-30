package me.dotu.JobTrackerBE.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import me.dotu.JobTrackerBE.Entity.TagEntity;
import me.dotu.JobTrackerBE.Repository.TagRepository;
import me.dotu.JobTrackerBE.Response.TagResponse;

@Service
public class TagService {
  @Autowired
  TagRepository tagRepo;

  private static final Set<String> VALID_SCOPES = Set.of("CONTACT", "APPLICATION");

  public List<TagResponse> getTagForUser(String clerkUserId){
    return tagRepo.findByUserId(clerkUserId)
    .stream()
    .map(TagResponse::from)
    .toList();
  }

  private boolean isValidColor(String colorHex){
    return colorHex != null && colorHex.matches("^#[0-9A-Fa-f]{6}$");
  }

  private boolean isValidName(String name){
    return name != null && !name.isBlank() && name.length() <= 50;
  }

  public ResponseEntity<TagResponse> createTag(String clerkUserId, String name, String colorHex, String scope){
    if (!isValidName(name) || !isValidColor(colorHex) || scope == null || !VALID_SCOPES.contains(scope.toUpperCase())){
      return ResponseEntity.badRequest().build();
    }

    String trimmedName = name.trim();
    String upperScope = scope.toUpperCase();

    if (tagRepo.existsByUserIdAndNameAndScope(clerkUserId, trimmedName, upperScope)){
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    TagEntity tagEntity = new TagEntity();
    tagEntity.setUserId(clerkUserId);
    tagEntity.setName(trimmedName);
    tagEntity.setColorHex(colorHex.toUpperCase());
    tagEntity.setScope(upperScope);

    TagEntity saved = tagRepo.save(tagEntity);
    return ResponseEntity.status(HttpStatus.CREATED).body(TagResponse.from(saved));
  }

  public ResponseEntity<TagResponse> updateTag(String clerkUserId, Long tagId, String name, String colorHex){
    Optional<TagEntity> tagEntityO = tagRepo.findByIdAndUserId(tagId, clerkUserId);

    if (tagEntityO.isEmpty()){
      return ResponseEntity.notFound().build();
    }

    if ((name != null && !isValidName(name)) || (colorHex != null && !isValidColor(colorHex))){
      return ResponseEntity.badRequest().build();
    }

    TagEntity tagEntity = tagEntityO.get();

    if (name != null){
      String trimmedName = name.trim();
      if (!trimmedName.equals(tagEntity.getName()) && tagRepo.existsByUserIdAndNameAndScope(clerkUserId, trimmedName, tagEntity.getScope())){
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
      }
      tagEntity.setName(trimmedName);
    }

    if (colorHex != null){
      tagEntity.setColorHex(colorHex.toUpperCase());
    }

    TagEntity saved = tagRepo.save(tagEntity);
    return ResponseEntity.ok(TagResponse.from(saved));
  }

  public ResponseEntity<Void> deleteTag(String clerkUserId, Long tagId){
    Optional<TagEntity> tagEntityO = tagRepo.findByIdAndUserId(tagId, clerkUserId);

    if (tagEntityO.isEmpty()){
      return ResponseEntity.notFound().build();
    }

    tagRepo.delete(tagEntityO.get());
    return ResponseEntity.noContent().build();
  }
}
