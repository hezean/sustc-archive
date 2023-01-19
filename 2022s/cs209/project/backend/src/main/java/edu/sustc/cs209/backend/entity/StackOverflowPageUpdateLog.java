package edu.sustc.cs209.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table
@ToString
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"}, ignoreUnknown = true)
public class StackOverflowPageUpdateLog {
    @Id
    private String position;

    private LocalDateTime time;
}
