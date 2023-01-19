package edu.sustc.cs209.backend.dao;

import edu.sustc.cs209.backend.entity.StackOverflowPageUpdateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StackOverflowPageUpdateLogDAO extends JpaRepository<StackOverflowPageUpdateLog, String> {
}
