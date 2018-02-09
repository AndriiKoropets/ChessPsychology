package com.koropets_suhanov.chess.model;

public interface Subject {

    void notify(Observer figure);
    void register(Observer figure);
    void removeFigure(Observer figure);
}