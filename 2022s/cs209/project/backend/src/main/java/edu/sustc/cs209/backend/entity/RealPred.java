package edu.sustc.cs209.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@Deprecated
public class RealPred {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

//    Map<LocalDate, Integer> real;
//    Map<LocalDate, Float> pred;
//
//    public RealPred(Map<LocalDate, Integer> real, Map<LocalDate, Float> pred) {
//        this.real = real;
//        this.pred = pred;
//    }
}
