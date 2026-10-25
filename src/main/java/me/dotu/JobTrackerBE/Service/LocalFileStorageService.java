package me.dotu.JobTrackerBE.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import me.dotu.JobTrackerBE.Config.StorageProperties;
import me.dotu.JobTrackerBE.Exception.StorageException;

@Service
public class LocalFileStorageService implements FileStorageService {
  private static final Logger log = LoggerFactory.getLogger(LocalFileStorageService.class);

  private final Path baseDir;

  public LocalFileStorageService(StorageProperties storageProperties) {
    if (storageProperties.baseDir() == null || storageProperties.baseDir().isBlank()) {
      throw new IllegalStateException("app.storage.base-dir must be set");
    }
    this.baseDir = Paths.get(storageProperties.baseDir()).toAbsolutePath().normalize();
    try {
      Files.createDirectories(baseDir);
    } catch (IOException e) {
      throw new IllegalStateException("Could not create storage directory", e);
    }
  }

  @Override
  public void store(InputStream in, String key) {
    Path target = resolve(key);
    try {
      Files.copy(in, target);
    } catch (FileAlreadyExistsException e) {
      throw new StorageException("Storage key collision", e);
    } catch (IOException e) {
      deleteQuietly(target);
      throw new StorageException("Failed to store file", e);
    }
  }

  @Override
  public InputStream load(String key) {
    try {
      return Files.newInputStream(resolve(key));
    } catch (NoSuchFileException e) {
      throw new StorageException("File missing from storage: " + key, e);
    } catch (IOException e) {
      throw new StorageException("Failed to read file", e);
    }
  }

  @Override
  public void delete(String key) {
    deleteQuietly(resolve(key));
  }

  private Path resolve(String key) {
    Path target = baseDir.resolve(key).normalize();
    if (!baseDir.equals(target.getParent())) {
      throw new StorageException("Invalid storage key");
    }
    return target;
  }

  private void deleteQuietly(Path path) {
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      log.warn("Could not delete file {}", path, e);
    }
  }
}
