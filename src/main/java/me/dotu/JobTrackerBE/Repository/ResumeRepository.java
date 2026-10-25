package me.dotu.JobTrackerBE.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import me.dotu.JobTrackerBE.Entity.ResumeEntity;

public interface ResumeRepository extends JpaRepository<ResumeEntity, Long> {
  List<ResumeEntity> findByUserIdOrderByUploadedAtDesc(String userId);
  Optional<ResumeEntity> findByIdAndUserId(Long id, String userId);
  boolean existsByIdAndUserId(Long id, String userId);
  long countByUserId(String userId);
  Optional<ResumeEntity> findFirstByUserIdOrderByUploadedAtDesc(String userId);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("UPDATE ResumeEntity r SET r.isDefault = false WHERE r.userId = :userId")
  void clearDefaultForUser(@Param("userId") String userId);
}
