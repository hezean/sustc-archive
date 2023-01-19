package edu.sustc.cs209.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Map;

@Data
@Entity
@Table
@Deprecated
public class IssueTracker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JsonIgnoreProperties
    private Long id;

//    Object openIssues;
//    Object closeIssues;
//
//    public IssueTracker(Object openIssues, Object closeIssues) {
//        this.openIssues = openIssues;
//        this.closeIssues = closeIssues;
//    }
}
