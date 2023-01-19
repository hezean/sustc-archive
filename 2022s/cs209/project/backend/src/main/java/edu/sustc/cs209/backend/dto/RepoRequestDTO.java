package edu.sustc.cs209.backend.dto;

import edu.sustc.cs209.backend.controller.GithubRepoController;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepoRequestDTO {

    String user;
    String repo;

    boolean forceUpdate = false;

    GithubRepoController.CountIssueByInterval interval = GithubRepoController.CountIssueByInterval.Day;

}
