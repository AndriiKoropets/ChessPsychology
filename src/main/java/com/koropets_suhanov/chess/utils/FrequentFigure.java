package com.koropets_suhanov.chess.utils;

/**
 * @author AndriiKoropets
 */
public class FrequentFigure {

    private int king;
    private int queen;
    private int bishop;
    private int knight;
    private int rock;
    private int pawn;

    FrequentFigure() {
        king = 0;
        queen = 0;
        bishop = 0;
        knight = 0;
        rock = 0;
        pawn = 0;
    }

    void updateKing(){
        this.king++;
    }

    void updateQueen(){
        this.queen++;
    }

    void updateBishop(){
        this.bishop++;
    }

    void updateKnight(){
        this.knight++;
    }

    void updateRock(){
        this.rock++;
    }

    void updatePawn(){
        this.pawn++;
    }

    public int getKing() {
        return king;
    }

    public int getQueen() {
        return queen;
    }

    public int getBishop() {
        return bishop;
    }

    public int getKnight() {
        return knight;
    }

    public int getRock() {
        return rock;
    }

    public int getPawn() {
        return pawn;
    }

    @Override
    public String toString() {
        return "FrequentFigure{" +
                "king=" + king +
                ", queen=" + queen +
                ", bishop=" + bishop +
                ", knight=" + knight +
                ", rock=" + rock +
                ", pawn=" + pawn +
                '}';
    }
}
