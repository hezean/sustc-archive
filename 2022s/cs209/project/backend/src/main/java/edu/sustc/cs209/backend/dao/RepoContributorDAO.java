package edu.sustc.cs209.backend.dao;

import edu.sustc.cs209.backend.entity.GithubRepo;
import edu.sustc.cs209.backend.entity.RepoContributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepoContributorDAO extends JpaRepository<RepoContributor, String> {

    List<RepoContributor> findByRepo(GithubRepo repo);

}
