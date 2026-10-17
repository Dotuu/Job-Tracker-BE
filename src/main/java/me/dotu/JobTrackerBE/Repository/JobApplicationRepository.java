package me.dotu.JobTrackerBE.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import me.dotu.JobTrackerBE.Entity.JobApplicationEntity;

public interface JobApplicationRepository extends JpaRepository<JobApplicationEntity, Long>{
  List<JobApplicationEntity> findByUserId(String userId);
  Optional<JobApplicationEntity> findByIdAndUserId(Long id, String userId);

  @Query("""
    SELECT j FROM JobApplicationEntity j
      WHERE j.userId = :userId
        AND (LOWER(j.companyName) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(j.roleTitle) LIKE LOWER(CONCAT('%', :search, '%'))
          OR CAST(j.dateApplied AS string) LIKE CONCAT('%', :search, '%')
          OR LOWER(j.location) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """)
    List<JobApplicationEntity> searchByUserId(@Param("userId") String userId, @Param("search") String search);

    @Query("""
    SELECT j FROM JobApplicationEntity j
      WHERE j.userId = :userId
        AND (LOWER(REPLACE(CAST(j.status AS string), '_', ' ')) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(REPLACE(CAST(j.locationType AS string), '_', ' ')) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(j.source) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """)
    List<JobApplicationEntity> searchTagsByUserId(@Param("userId") String userId, @Param("search") String search);
  }
