package me.dotu.JobTrackerBE.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import me.dotu.JobTrackerBE.Entity.StatusHistoryEntity;

public interface StatusHistoryRepository extends JpaRepository<StatusHistoryEntity, Long> {
  List<StatusHistoryEntity> findByJobApplicationIdOrderByChangedAtAsc(Long jobApplicationId);
  List<StatusHistoryEntity> findAllByUserId(String userId);
}
