package com.koropets_suhanov.chess.process.dto;

import com.koropets_suhanov.chess.model.Field;
import com.koropets_suhanov.chess.model.Figure;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FigureToField implements Serializable {
  private Figure figure;
  private Field field;
}
