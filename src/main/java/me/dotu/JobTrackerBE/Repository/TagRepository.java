package me.dotu.JobTrackerBE.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import me.dotu.JobTrackerBE.Entity.TagEntity;

public interface TagRepository extends JpaRepository<TagEntity, Long>{
  List<TagEntity> findByUserId(String userId);
  Optional<TagEntity> findByIdAndUserId(Long id, String userId);
  List<TagEntity> findAllByIdInAndUserId(Set<Long> ids, String userId);
  boolean existsByUserIdAndNameAndScope(String userId, String name, String scope);
}
