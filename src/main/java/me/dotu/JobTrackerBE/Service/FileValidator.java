package me.dotu.JobTrackerBE.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import me.dotu.JobTrackerBE.Config.StorageProperties;
import me.dotu.JobTrackerBE.Exception.InvalidFileException;

@Component
public class FileValidator {
  private static final Map<String, String> ALLOWED_TYPES = Map.of("application/pdf", ".pdf");
  private static final Pattern UNSAFE_CHARS = Pattern.compile("[^\\p{L}\\p{M}\\p{N} ._()-]");
  private static final int MAX_NAME_LENGTH = 200;

  private final Tika tika = new Tika();

  @Autowired
  private StorageProperties storageProperties;

  public record ValidatedFile(String contentType, String extension) {}

  public ValidatedFile validate(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new InvalidFileException(HttpStatus.BAD_REQUEST, "File is empty");
    }
    if (file.getSize() > storageProperties.maxFileSize().toBytes()) {
      throw new InvalidFileException(HttpStatus.CONTENT_TOO_LARGE, "File is too large");
    }

    String detected;
    try (InputStream in = new BufferedInputStream(file.getInputStream())) {
      detected = tika.detect(in);
    } catch (IOException e) {
      throw new InvalidFileException(HttpStatus.BAD_REQUEST, "Could not read file");
    }

    String extension = ALLOWED_TYPES.get(detected);
    if (extension == null) {
      throw new InvalidFileException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Only PDF files are allowed");
    }

    String original = file.getOriginalFilename();
    if (original == null || !original.toLowerCase(Locale.ROOT).endsWith(extension)) {
      throw new InvalidFileException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File extension does not match its contents");
    }

    return new ValidatedFile(detected, extension);
  }

  public String sanitizeDisplayName(String original, String extension) {
    String name = original == null ? "" : original;

    name = Normalizer.normalize(name, Normalizer.Form.NFC);

    name = name.replace('\\', '/');
    name = name.substring(name.lastIndexOf('/') + 1);

    if (name.toLowerCase(Locale.ROOT).endsWith(extension)) {
      name = name.substring(0, name.length() - extension.length());
    }
    name = UNSAFE_CHARS.matcher(name).replaceAll("_").trim();
    name = name.replaceAll("^\\.+", "");

    if (name.length() > MAX_NAME_LENGTH) {
      name = name.substring(0, MAX_NAME_LENGTH);
    }
    if (name.isBlank()) {
      name = "resume";
    }
    return name + extension;
  }
}
