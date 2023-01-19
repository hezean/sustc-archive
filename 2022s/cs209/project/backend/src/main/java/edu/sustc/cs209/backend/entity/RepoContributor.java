package edu.sustc.cs209.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.sustc.cs209.backend.util.GeoService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.net.URLEncoder;

@Data
@Entity
@Table
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"}, ignoreUnknown = true)
public class RepoContributor {

    @Id
    private String uri;

    private Long repo;

    @ManyToOne(cascade = CascadeType.MERGE)
    private GithubUser user;

    private String country;
    private long contributions;

    public RepoContributor(GithubRepo repo, GithubUser user, long contributions) {
        this.uri = String.format("%s/%s/%s", URLEncoder.encode(repo.getOwner().getUsername()), repo.getName(), URLEncoder.encode(user.getUsername()));
        this.repo = repo.getId();
        this.user = user;
        this.contributions = contributions;
        this.country = GeoService.get(this.user.getLocation());
    }
}
