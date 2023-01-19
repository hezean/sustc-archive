package edu.sustc.cs209.backend.dao;

import edu.sustc.cs209.backend.entity.GithubRepo;
import edu.sustc.cs209.backend.entity.GithubRepoAnalyze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GithubRepoAnalyzeDAO extends JpaRepository<GithubRepoAnalyze, GithubRepo> {

}
