package com.koropets_suhanov.chess.process.pojo;

public class FinalResult {

    private int first;
    private int second;
    private int third;
    private int fourth;
    private int fifth;
    private int sixth;
    private int seventh;
    private int eighth;

    private FinalResult(int first, int second, int third, int fourth, int fifth, int sixth, int seventh, int eighth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
        this.sixth = sixth;
        this.seventh = seventh;
        this.eighth = eighth;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getThird() {
        return third;
    }

    public void setThird(int third) {
        this.third = third;
    }

    public int getFourth() {
        return fourth;
    }

    public void setFourth(int fourth) {
        this.fourth = fourth;
    }

    public int getFifth() {
        return fifth;
    }

    public void setFifth(int fifth) {
        this.fifth = fifth;
    }

    public int getSixth() {
        return sixth;
    }

    public void setSixth(int sixth) {
        this.sixth = sixth;
    }

    public int getSeventh() {
        return seventh;
    }

    public void setSeventh(int seventh) {
        this.seventh = seventh;
    }

    public int getEighth() {
        return eighth;
    }

    public void setEighth(int eighth) {
        this.eighth = eighth;
    }

    @Override
    public String toString() {
        return "FinalResult{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                ", fourth=" + fourth +
                ", fifth=" + fifth +
                ", sixth=" + sixth +
                ", seventh=" + seventh +
                ", eighth=" + eighth +
                '}';
    }

    public static final class Builder{

        private int first;
        private int second;
        private int third;
        private int fourth;
        private int fifth;
        private int sixth;
        private int seventh;
        private int eighth;

        public Builder first(final int first){
            this.first = first;
            return this;
        }

        public Builder second(final int second){
            this.second = second;
            return this;
        }

        public Builder third(final int third){
            this.third = third;
            return this;
        }

        public Builder fourth(final int fourth){
            this.fourth = fourth;
            return this;
        }

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

        public FinalResult build(){
            return new FinalResult(first, second, third, fourth, fifth, sixth, seventh, eighth);
        }
    }
}
