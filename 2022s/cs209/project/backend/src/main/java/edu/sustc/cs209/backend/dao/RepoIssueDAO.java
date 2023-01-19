package edu.sustc.cs209.backend.dao;

import edu.sustc.cs209.backend.entity.RepoIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepoIssueDAO extends JpaRepository<RepoIssue, Long> {

    Optional<RepoIssue> findById(Long id);

    Long countByCreatedAtBetween(LocalDateTime st, LocalDateTime ed);

    Long countByClosedAtBetween(LocalDateTime st, LocalDateTime ed);

    List<RepoIssue> findByLabels_id(Long id);

}
