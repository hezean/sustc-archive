package edu.sustc.cs209.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BarChartDisplayDTO {

    private Object time;
    private Integer amount;

}
