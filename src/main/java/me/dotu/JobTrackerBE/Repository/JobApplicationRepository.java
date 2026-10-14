package me.dotu.JobTrackerBE.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import me.dotu.JobTrackerBE.Entity.JobApplicationEntity;

public interface JobApplicationRepository extends JpaRepository<JobApplicationEntity, Long>{
  List<JobApplicationEntity> findByUserId(String userId);
  Optional<JobApplicationEntity> findByIdAndUserId(Long id, String userId);
}
