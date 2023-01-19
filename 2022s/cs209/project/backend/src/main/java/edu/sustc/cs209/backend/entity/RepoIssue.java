package edu.sustc.cs209.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
@ToString
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"}, ignoreUnknown = true)
public class RepoIssue {

    @Id
    private Long id;

    private String title;

    @OrderColumn
    private String[] titleNouns;
    @OrderColumn
    private String[] titleVerbs;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Nullable
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("closed_at")
    private LocalDateTime closedAt;

    private IssueState state;

    @ManyToMany(cascade = CascadeType.MERGE)
    @ToString.Exclude
    private Set<RepoIssueLabel> labels;

    public enum IssueState {
        open, closed
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RepoIssue repoIssue = (RepoIssue) o;
        return id != null && Objects.equals(id, repoIssue.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
