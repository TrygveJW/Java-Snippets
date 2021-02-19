package no.trygvejw.dataHolders;

/**
 * Utillity class representing an integer range.
 * The values are final and cannot be changed
 */
public class Range{
    public final int from;
    public final int to;

    /**
     * Creates the range
     * @param from the from value
     * @param to the to value
     */
    public Range(int from, int to) {
        this.from = from;
        this.to   = to;
    }
}