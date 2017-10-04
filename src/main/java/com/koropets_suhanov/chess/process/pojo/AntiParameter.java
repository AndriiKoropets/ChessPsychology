package com.koropets_suhanov.chess.process.pojo;

public class AntiParameter {

    private int fifthParam;
    private int sixthParam;
    private int seventhParam;
    private int eighthParam;

    private AntiParameter(int fifthParam, int sixthParam, int seventhParam, int eighthParam){
        this.fifthParam = fifthParam;
        this.sixthParam = sixthParam;
        this.seventhParam = seventhParam;
        this.eighthParam = eighthParam;
    }

    public int getFifthParam() {
        return fifthParam;
    }

    public int getSixthParam() {
        return sixthParam;
    }

    public int getSeventhParam() {
        return seventhParam;
    }

    public int getEighthParam() {
        return eighthParam;
    }

    @Override
    public String toString() {
        return "AntiParameter{" +
                "fifthParam=" + fifthParam +
                ", sixthParam=" + sixthParam +
                ", seventhParam=" + seventhParam +
                ", eighthParam=" + eighthParam +
                '}';
    }

    public static final class Builder{

        private int fifth;
        private int sixth;
        private int seventh;
        private int eighth;

        public Builder fifth(final int fifth){
            this.fifth = fifth;
            return this;
        }

        public Builder sixth(final int sixth){
            this.sixth = sixth;
            return this;
        }

        public Builder seventh(final int seventh){
            this.seventh = seventh;
            return this;
        }

        public Builder eighth(final int eighth){
            this.eighth = eighth;
            return this;
        }

        public AntiParameter build(){
            return new AntiParameter(fifth, sixth, seventh, eighth);
        }
    }
}
