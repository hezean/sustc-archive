package edu.sustc.cs209.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class IssueTrackerDTO<T> {

//    Map<T, Integer> openIssues;
//    Map<T, Integer> closeIssues;

    List<BarChartDisplayDTO> openIssues;
    List<BarChartDisplayDTO> closeIssues;

}
