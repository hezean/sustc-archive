package edu.sustc.cs209.backend.dao;

import edu.sustc.cs209.backend.entity.GithubUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GithubUserDAO extends JpaRepository<GithubUser, Long> {
}
