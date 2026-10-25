package me.dotu.JobTrackerBE.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import me.dotu.JobTrackerBE.Config.StorageProperties;
import me.dotu.JobTrackerBE.Entity.ResumeEntity;
import me.dotu.JobTrackerBE.Exception.StorageException;
import me.dotu.JobTrackerBE.Exception.UploadLimitException;
import me.dotu.JobTrackerBE.Repository.ResumeRepository;
import me.dotu.JobTrackerBE.Response.ResumeResponse;

@Service
public class ResumeService {
  private static final Logger log = LoggerFactory.getLogger(ResumeService.class);

  @Autowired
  private ResumeRepository resumeRepo;

  @Autowired
  private FileStorageService storage;

  @Autowired
  private FileValidator fileValidator;

  @Autowired
  private StorageProperties storageProperties;

  public List<ResumeResponse> getResumesForUser(String clerkUserId) {
    return resumeRepo.findByUserIdOrderByUploadedAtDesc(clerkUserId)
    .stream()
    .map(ResumeResponse::from)
    .toList();
  }

  public ResponseEntity<ResumeResponse> uploadResume(String clerkUserId, MultipartFile file, String title) {
    long existing = resumeRepo.countByUserId(clerkUserId);
    if (existing >= storageProperties.maxFilesPerUser()) {
      throw new UploadLimitException("You can store up to " + storageProperties.maxFilesPerUser() + " resumes");
    }

    FileValidator.ValidatedFile validated = fileValidator.validate(file);
    String key = UUID.randomUUID() + validated.extension();

    String sha256;
    try (DigestInputStream in = new DigestInputStream(file.getInputStream(), MessageDigest.getInstance("SHA-256"))) {
      storage.store(in, key);
      sha256 = HexFormat.of().formatHex(in.getMessageDigest().digest());
    } catch (IOException | NoSuchAlgorithmException e) {
      storage.delete(key);
      throw new StorageException("Failed to store file", e);
    }

    ResumeEntity resume = new ResumeEntity();
    resume.setUserId(clerkUserId);
    String displayName = (title == null || title.isBlank()) ? file.getOriginalFilename() : title;
    resume.setFileName(fileValidator.sanitizeDisplayName(displayName, validated.extension()));
    resume.setStoragePath(key);
    resume.setContentType(validated.contentType());
    resume.setSizeBytes(file.getSize());
    resume.setSha256(sha256);
    resume.setIsDefault(existing == 0);

    ResumeEntity saved;
    try {
      saved = resumeRepo.save(resume);
    } catch (RuntimeException e) {
      storage.delete(key);
      throw e;
    }

    log.info("Resume uploaded user={} resumeId={} size={}", clerkUserId, saved.getId(), saved.getSizeBytes());
    return ResponseEntity.status(HttpStatus.CREATED).body(ResumeResponse.from(saved));
  }

  public ResponseEntity<Resource> downloadResume(String clerkUserId, Long resumeId) {
    Optional<ResumeEntity> resumeO = resumeRepo.findByIdAndUserId(resumeId, clerkUserId);

    if (resumeO.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    ResumeEntity resume = resumeO.get();
    InputStream in = storage.load(resume.getStoragePath());

    return ResponseEntity.ok()
      .contentType(MediaType.parseMediaType(resume.getContentType()))
      .contentLength(resume.getSizeBytes())
      .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
        .filename(resume.getFileName(), StandardCharsets.UTF_8)
        .build()
        .toString())
      .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
      .body(new InputStreamResource(in));
  }

  public ResponseEntity<ResumeResponse> renameResume(String clerkUserId, Long resumeId, String fileName) {
    Optional<ResumeEntity> resumeO = resumeRepo.findByIdAndUserId(resumeId, clerkUserId);

    if (resumeO.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    if (fileName == null || fileName.isBlank()) {
      return ResponseEntity.badRequest().build();
    }

    ResumeEntity resume = resumeO.get();
    String key = resume.getStoragePath();
    String extension = key.substring(key.lastIndexOf('.'));
    resume.setFileName(fileValidator.sanitizeDisplayName(fileName, extension));

    return ResponseEntity.ok(ResumeResponse.from(resumeRepo.save(resume)));
  }

  @Transactional
  public ResponseEntity<ResumeResponse> setDefaultResume(String clerkUserId, Long resumeId) {
    Optional<ResumeEntity> resumeO = resumeRepo.findByIdAndUserId(resumeId, clerkUserId);

    if (resumeO.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    resumeRepo.clearDefaultForUser(clerkUserId);
    ResumeEntity resume = resumeO.get();
    resume.setIsDefault(true);

    return ResponseEntity.ok(ResumeResponse.from(resumeRepo.save(resume)));
  }

  public ResponseEntity<Void> deleteResume(String clerkUserId, Long resumeId) {
    Optional<ResumeEntity> resumeO = resumeRepo.findByIdAndUserId(resumeId, clerkUserId);

    if (resumeO.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    ResumeEntity resume = resumeO.get();
    resumeRepo.delete(resume);
    if (resume.isDefault()) {
      resumeRepo.findFirstByUserIdOrderByUploadedAtDesc(clerkUserId).ifPresent(next -> {
        next.setIsDefault(true);
        resumeRepo.save(next);
      });
    }

    storage.delete(resume.getStoragePath());

    log.info("Resume deleted user={} resumeId={}", clerkUserId, resumeId);
    return ResponseEntity.noContent().build();
  }
}
