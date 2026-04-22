package com.fractalflame.model;

public final class Pixel {
    private long rSum;
    private long gSum;
    private long bSum;
    private int hits;

    public void hit(int r, int g, int b) {
        rSum += r;
        gSum += g;
        bSum += b;
        hits++;
    }

    public void merge(Pixel other) {
        this.rSum += other.rSum;
        this.gSum += other.gSum;
        this.bSum += other.bSum;
        this.hits += other.hits;
    }

    public int hits() { return hits; }

    public int avgR() { return hits == 0 ? 0 : (int) (rSum / hits); }
    public int avgG() { return hits == 0 ? 0 : (int) (gSum / hits); }
    public int avgB() { return hits == 0 ? 0 : (int) (bSum / hits); }
}
