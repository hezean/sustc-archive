package edu.sustc.cs209.backend.dao;

import edu.sustc.cs209.backend.entity.StackOverflowTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StackOverflowTagDAO extends JpaRepository<StackOverflowTag, Integer> {
}
