package edu.sustc.cs209.backend.dao;

import edu.sustc.cs209.backend.entity.StackOverflowQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StackOverflowQuestionDAO extends JpaRepository<StackOverflowQuestion, Long> {

    List<StackOverflowQuestion> findByPosition(String position);

    void deleteByPosition(String position);

}
