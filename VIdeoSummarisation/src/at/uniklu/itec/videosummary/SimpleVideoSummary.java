package at.uniklu.itec.videosummary;

import java.util.ArrayList;

/**
 * User: mlux
 * Date: 17.04.2008
 * Time: 11:20:31
 * Used to create a Videosummary
 */
public class SimpleVideoSummary implements VideoSummary {
    ArrayList<int[]> hists;

    public SimpleVideoSummary() {
        hists = new ArrayList<int[]>(50);
    }

    public void analyzeFrame(int frameNumber, int[][][] rgbValues) {
        int[] hist = new int[64];
        for (int i = 0; i < hist.length; i++) {
            hist[i] = 0;
        }
        for (int i = 0; i < rgbValues.length; i++) {
            int[][] rgbValue = rgbValues[i];
            for (int j = 0; j < rgbValue.length; j++) {
                int[] rgb = rgbValue[j];
                hist[quant(rgb[0]) * 4 * 4 + quant(rgb[1]) * 4 + quant(rgb[2])]++;
            }
        }
        hists.add(hist);
    }

    public int[] getSummaryFrames() {
        int[] ints = new int[1];
        ints[0] = hists.size() / 2;
        return ints;
    }

    private double dist(int[] h1, int[] h2) {
        double result = 0;
        // L1
        for (int i = 0; i < h1.length; i++) {
            result += Math.abs(h1[i] - h2[i]);
        }
        // L2
//        for (int i = 0; i < h1.length; i++) {
//            result += (h1[i] - h2[i]) * (h1[i] - h2[i]);
//
//        }
//        return Math.sqrt(result);
        return result;
    }

    private int quant(int val) {
        if (val < 256 / 4) return 0;
        else if (val < 256 / 2) return 1;
        else if (val < 3 * 256 / 4) return 2;
        else return 3;
    }
}
