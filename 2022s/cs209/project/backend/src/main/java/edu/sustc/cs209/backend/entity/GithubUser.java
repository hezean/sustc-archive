package edu.sustc.cs209.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"}, ignoreUnknown = true)
public class GithubUser {

    @Id
    private Long id;

    @JsonProperty("login")
    private String username;

    @JsonProperty("name")
    private String nickname;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    private String company;
    private String blog;

    @Nullable
    private String email;

    @Nullable
    private String location;

    private Integer followers;
    private Integer following;

    @JsonProperty("public_repos")
    private Integer publicRepos;

    private Date infoUpdatedAt;

}
