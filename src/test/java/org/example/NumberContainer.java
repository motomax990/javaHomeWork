package org.example;

import java.util.List;

public class NumberContainer {

    private List<Integer> ints;
    private List<Long> longs;
    private List<Double> doubles;
    private List<Boolean> bools;

    public NumberContainer() {
    }

    public List<Integer> getInts() { return ints; }
    public void setInts(List<Integer> v) { this.ints = v; }

    public List<Long> getLongs() { return longs; }
    public void setLongs(List<Long> v) { this.longs = v; }

    public List<Double> getDoubles() { return doubles; }
    public void setDoubles(List<Double> v) { this.doubles = v; }

    public List<Boolean> getBools() { return bools; }
    public void setBools(List<Boolean> v) { this.bools = v; }
}
