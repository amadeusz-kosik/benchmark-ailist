package benchmark.ailist.java;

import benchmark.Interval;

import java.util.ArrayList;


public class DataGenerator {

    public static ArrayList<Interval> consecutive(final int rowsCount) {
        return intervals(rowsCount, 2, 0, new ArrayList<>(rowsCount));
    }

    private static ArrayList<Interval> intervals(final int rowsCount, final int width, final int overlap, final ArrayList<Interval> output) {
        assert rowsCount >  0;
        assert width     >= 0;
        assert overlap   >= 0;

        for(int i = 0; i < rowsCount; i++) {
            final long from = (long) i * width - (overlap / 2);
            final long to   = (long) from + width - 1 + overlap;

            output.add(new Interval(from, to));
        }

        return output;
    }
}
