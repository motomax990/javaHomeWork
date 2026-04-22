package com.fractalflame.render;

import com.fractalflame.model.Pixel;

public final class FractalImage {
    private final int width;
    private final int height;
    private final long[] rSum;
    private final long[] gSum;
    private final long[] bSum;
    private final int[] hits;

    public FractalImage(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Размер изображения должен быть положительным");
        }
        this.width = width;
        this.height = height;
        int n = width * height;
        this.rSum = new long[n];
        this.gSum = new long[n];
        this.bSum = new long[n];
        this.hits = new int[n];
    }

    public int width() { return width; }
    public int height() { return height; }

    public boolean contains(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public void hit(int x, int y, int r, int g, int b) {
        int i = y * width + x;
        rSum[i] += r;
        gSum[i] += g;
        bSum[i] += b;
        hits[i]++;
    }

    public Pixel pixel(int x, int y) {
        int i = y * width + x;
        Pixel p = new Pixel();
        if (hits[i] > 0) {
            int r = (int) (rSum[i] / hits[i]);
            int g = (int) (gSum[i] / hits[i]);
            int b = (int) (bSum[i] / hits[i]);
            for (int k = 0; k < hits[i]; k++) p.hit(r, g, b);
        }
        return p;
    }

    public int hitsAt(int x, int y) { return hits[y * width + x]; }
    public int avgR(int x, int y) { int i = y*width+x; return hits[i]==0?0:(int)(rSum[i]/hits[i]); }
    public int avgG(int x, int y) { int i = y*width+x; return hits[i]==0?0:(int)(gSum[i]/hits[i]); }
    public int avgB(int x, int y) { int i = y*width+x; return hits[i]==0?0:(int)(bSum[i]/hits[i]); }

    public void merge(FractalImage other) {
        if (other.width != width || other.height != height) {
            throw new IllegalArgumentException("Размеры изображений не совпадают");
        }
        int n = width * height;
        for (int i = 0; i < n; i++) {
            rSum[i] += other.rSum[i];
            gSum[i] += other.gSum[i];
            bSum[i] += other.bSum[i];
            hits[i] += other.hits[i];
        }
    }
}
