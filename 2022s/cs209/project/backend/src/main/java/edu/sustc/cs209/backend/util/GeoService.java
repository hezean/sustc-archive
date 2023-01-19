package edu.sustc.cs209.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.Nullable;

import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class GeoService {

    private static final String TOKEN = "bc02f055068cee847e78dfbfad252b09";
    private static final ObjectMapper mapper = new ObjectMapper();


    @SuppressWarnings("unchecked")
    public static String get(@Nullable String qry) {
        if (qry == null) return "N/A";

        try (var os = new URL(String.format("http://api.positionstack.com/v1/forward?access_key=%s&query=%s", TOKEN, URLEncoder.encode(qry))).openStream()) {
            var res = mapper.readValue(os, Map.class);
            List<Map<String, Object>> data = (List<Map<String, Object>>) res.get("data");
            if (data.isEmpty()) return "N/A";

            return (String) data.get(0).get("country");
        } catch (Exception e) {
            return "N/A";
        }
    }

}
