package me.dotu.JobTrackerBE.Service;

import java.io.InputStream;

public interface FileStorageService {
  void store(InputStream in, String key);
  InputStream load(String key);
  void delete(String key);
}
