package edu.sustc.cs209.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"title"}))
@ToString
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"}, ignoreUnknown = true)
public class StackOverflowTag {
    @Id
    private Integer id;

    private String title;

    public StackOverflowTag(String title) {
        this.title = title;
        this.id = title.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StackOverflowTag that = (StackOverflowTag) o;
        return id != null || Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }
}
