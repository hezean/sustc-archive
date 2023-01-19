package edu.sustc.cs209.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.sustc.cs209.backend.entity.GithubRepo;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class GithubRepoFactory {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final AuthGithubApiRequest webProxy = new AuthGithubApiRequest();

    private static final Logger logger = Logger.getLogger(String.valueOf(GithubRepoFactory.class));

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static GithubRepo $(String owner, String name) {
        owner = URLEncoder.encode(owner.trim());
        name = URLEncoder.encode(name.trim());
        return $(String.format("%s/%s", owner, name));
    }

    public static GithubRepo $(String fullName) {
        try {
            logger.info("Fetching API:: " + fullName);
            return webProxy.get(String.format("https://api.github.com/repos/%s", fullName), null, GithubRepo.class).get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
