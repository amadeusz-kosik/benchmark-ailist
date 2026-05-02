package benchmark.ailist.java;


public record Interval(
        long from,
        long to
) {

    public static boolean overlaps(final Interval lhs, final Interval rhs) {
        return lhs.from <= rhs.to && rhs.from <= lhs.to;
    }
}