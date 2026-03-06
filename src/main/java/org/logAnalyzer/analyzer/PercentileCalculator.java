package org.logAnalyzer.analyzer;

public class PercentileCalculator {

    private final double p;

    private final double[] q = new double[5];
    private final long[] n = new long[5];
    private final double[] nDesired = new double[5];

    private long count = 0;

    private final double[] initBuffer = new double[5];

    public PercentileCalculator(double percentile) {
        if (percentile <= 0 || percentile >= 1) {
            throw new IllegalArgumentException("Percentile must be in (0, 1), got: " + percentile);
        }
        this.p = percentile;
    }

    public void add(double value) {
        if (count < 5) {
            initBuffer[(int) count] = value;
            count++;
            if (count == 5) {
                initMarkers();
            }
            return;
        }

        count++;
        int k = findCell(value);
        updatePositions(k);
        adjustMarkers();
    }

    public double getEstimate() {
        if (count == 0) {
            return 0.0;
        }
        if (count < 5) {
            return estimateFromBuffer();
        }
        return Math.round(q[2] * 100.0) / 100.0;
    }

    private void initMarkers() {
        double[] sorted = sortedCopy(initBuffer);

        q[0] = sorted[0];
        q[1] = sorted[1];
        q[2] = sorted[2];
        q[3] = sorted[3];
        q[4] = sorted[4];

        n[0] = 1;
        n[1] = 2;
        n[2] = 3;
        n[3] = 4;
        n[4] = 5;

        nDesired[0] = 1;
        nDesired[1] = 1 + 2 * p;
        nDesired[2] = 1 + 4 * p;
        nDesired[3] = 3 + 2 * p;
        nDesired[4] = 5;
    }

    private int findCell(double value) {
        if (value < q[0]) {
            q[0] = value;
            return 0;
        }
        for (int i = 0; i < 4; i++) {
            if (q[i] <= value && value < q[i + 1]) {
                return i;
            }
        }
        q[4] = value;
        return 3;
    }

    private void updatePositions(int k) {
        for (int i = k + 1; i <= 4; i++) {
            n[i]++;
        }

        double total = count;
        nDesired[0] = 1;
        nDesired[1] = 1 + (total - 1) * (p / 2.0);
        nDesired[2] = 1 + (total - 1) * p;
        nDesired[3] = 1 + (total - 1) * ((1 + p) / 2.0);
        nDesired[4] = total;
    }

    private void adjustMarkers() {
        for (int i = 1; i <= 3; i++) {
            double d = nDesired[i] - n[i];
            if ((d >= 1 && n[i + 1] - n[i] > 1) || (d <= -1 && n[i - 1] - n[i] < -1)) {
                int sign = d > 0 ? 1 : -1;
                double qParabolic = parabolic(i, sign);
                if (q[i - 1] < qParabolic && qParabolic < q[i + 1]) {
                    q[i] = qParabolic;
                } else {
                    q[i] = linear(i, sign);
                }
                n[i] += sign;
            }
        }
    }

    private double parabolic(int i, int sign) {
        double left  = (double) sign / (n[i + 1] - n[i - 1]);
        double right1 = (n[i] - n[i - 1] + sign) * (q[i + 1] - q[i]) / (double) (n[i + 1] - n[i]);
        double right2 = (n[i + 1] - n[i] - sign) * (q[i] - q[i - 1]) / (double) (n[i] - n[i - 1]);
        return q[i] + left * (right1 + right2);
    }

    private double linear(int i, int sign) {
        int j = i + sign;
        return q[i] + sign * (q[j] - q[i]) / (double) (n[j] - n[i]);
    }

    private double estimateFromBuffer() {
        double[] sorted = sortedCopy(initBuffer, (int) count);
        int idx = (int) Math.max(0, Math.ceil(p * count) - 1);
        return sorted[idx];
    }

    private double[] sortedCopy(double[] arr) {
        return sortedCopy(arr, arr.length);
    }

    private double[] sortedCopy(double[] arr, int length) {
        double[] copy = new double[length];
        System.arraycopy(arr, 0, copy, 0, length);
        for (int i = 1; i < length; i++) {
            double key = copy[i];
            int j = i - 1;
            while (j >= 0 && copy[j] > key) {
                copy[j + 1] = copy[j];
                j--;
            }
            copy[j + 1] = key;
        }
        return copy;
    }
}
