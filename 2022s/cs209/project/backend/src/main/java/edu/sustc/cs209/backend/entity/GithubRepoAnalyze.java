package edu.sustc.cs209.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"}, ignoreUnknown = true)
@Deprecated
public class GithubRepoAnalyze {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    private GithubRepo repo;

    @OneToOne
    private IssueTracker issueCntByDay;

    @OneToOne
    private IssueTracker issueCntByMonth;

    @OneToOne
    private IssueTracker issueCntByYear;

    @OneToOne
    private RealPred issueDayPred;

}
