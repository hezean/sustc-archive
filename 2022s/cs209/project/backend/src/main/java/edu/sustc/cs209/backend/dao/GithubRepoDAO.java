package edu.sustc.cs209.backend.dao;

import edu.sustc.cs209.backend.entity.GithubRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GithubRepoDAO extends JpaRepository<GithubRepo, Long> {

    Optional<GithubRepo> findByNameAndOwner_UsernameAllIgnoreCase(String name, String username);

}
