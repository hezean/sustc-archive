package edu.sustc.cs209.backend.dto;

import edu.sustc.cs209.backend.entity.RepoIssueLabel;
import lombok.Data;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Data
public class IssueSummaryDTO {
    private String name;
    private String description;
    private String color;

    private List<Map<String, Object>> data;
    private List<Map<String, Object>> data1;
    private Long data3;

    public IssueSummaryDTO(RepoIssueLabel label, Map<YearMonth, Integer> data, Map<Year, Integer> data1) {
        this.name = label.getName();
        this.description = label.getDescription();
        this.color = label.getColor();
        this.data = data.entrySet().stream()
                .map(e -> Map.of("time", e.getKey(), "amount", (Object) e.getValue()))
                .toList();
        this.data1 = data1.entrySet().stream()
                .map(e -> Map.of("time", e.getKey(), "amount", (Object) e.getValue()))
                .toList();
        this.data3 = this.data1.stream().mapToLong(e -> (int) e.get("amount")).sum();
    }
}
