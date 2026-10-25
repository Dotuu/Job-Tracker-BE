package me.dotu.JobTrackerBE.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties (
  String baseDir,
  @DefaultValue("5MB") DataSize maxFileSize,
  @DefaultValue("10") int maxFilesPerUser
) {}