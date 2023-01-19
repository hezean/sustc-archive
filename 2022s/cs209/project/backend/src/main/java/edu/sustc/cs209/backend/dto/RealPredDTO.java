package edu.sustc.cs209.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealPredDTO {

    Map<LocalDate, Integer> real;
    Map<LocalDate, Float> pred;

}
