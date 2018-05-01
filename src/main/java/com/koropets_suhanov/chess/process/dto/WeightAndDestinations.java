package com.koropets_suhanov.chess.process.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WeightAndDestinations implements Serializable {
  private int weight;
  private List<FigureToField> figureToFields;
}
