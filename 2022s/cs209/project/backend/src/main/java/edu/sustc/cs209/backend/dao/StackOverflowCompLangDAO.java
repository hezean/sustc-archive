package edu.sustc.cs209.backend.dao;

import edu.sustc.cs209.backend.entity.StackOverflowCompLang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StackOverflowCompLangDAO extends JpaRepository<StackOverflowCompLang, String> {
}
