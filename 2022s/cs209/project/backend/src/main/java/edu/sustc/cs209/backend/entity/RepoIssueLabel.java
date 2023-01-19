package edu.sustc.cs209.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Data
@Entity
@Table(name = "issue_labels", uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"}, ignoreUnknown = true)
public class RepoIssueLabel implements Comparable<RepoIssueLabel> {

    @Id
    private Long id;

    private String name;
    private String description;
    private String color;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(RepoIssueLabel o) {
        return Long.compare(this.id, o.id);
    }
}
