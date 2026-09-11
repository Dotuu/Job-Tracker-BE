package me.dotu.JobTrackerBE.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.dotu.JobTrackerBE.Entity.JobApplicationEntity;

public interface JobApplicationRepository extends JpaRepository<JobApplicationEntity, Long>{
  
}
