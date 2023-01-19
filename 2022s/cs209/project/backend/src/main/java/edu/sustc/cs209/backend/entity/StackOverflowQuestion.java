package edu.sustc.cs209.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table
@ToString
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"}, ignoreUnknown = true)
public class StackOverflowQuestion {

    @Id
    private Long postId;

    private String title;

    private Long votes;
    private Integer answers;
    private Long views;

    @OrderColumn
    private String[] titleNouns;
    @OrderColumn
    private String[] titleVerbs;


    @ManyToMany(cascade = CascadeType.MERGE)
    private List<StackOverflowTag> tags;

    @JsonIgnoreProperties
    private String position;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StackOverflowQuestion that = (StackOverflowQuestion) o;
        return postId != null && Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
