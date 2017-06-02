package net.hollowbit.archipeloshared.rangeindexer;

import java.util.ArrayList;

/**
 * Data structure that maps values to a range of values within Comparable objects.
 * It is similar to an array except the indexes are a range specified by Comparable objects, rather than a specific number.
 * Example using Integer ranges:
 * [0,5[ => "Apple"
 * [5,8[ => "Pair"
 * [8,31[ => "Banana"
 *
 * Ranges are handled by this data structure starting from the minimum. Therefore, ranges cannot be specified arbitrarily.
 * Ranges are based off the previously defined range (or the minimum if there are no previously defined ranges).
 *
 * @author Nathanael Maher
 *
 */
public class RangeMap<L extends Comparable<L>, V> {

    private ArrayList<RangeDefinition> values;
    private L minimum;

    public RangeMap(L minimum) {
        this.minimum = minimum;
        values = new ArrayList<RangeDefinition>();
    }

    /**
     * Maps a value to the range [previousLimit, nextLimit[
     * If no limit was previously specified, it will be mapped to [minimum, nextLimit[
     *
     * nextLimit must be greater than the previousLimit and the minimum.
     * @param nextLimit
     * @param value
     */
    public void add(L nextLimit, V value) {
        if (nextLimit.compareTo(getTopLimit()) > 0)
            values.add(new RangeDefinition(nextLimit, value));
        else
            throw new IllegalNextRangeLimitException();
    }

    /**
     * Returns the value of the range the given index falls into.
     * @param index
     * @return
     */
    public V getValue(L index) {
        //Index is less than minimum, return null.
        if (index.compareTo(minimum) < 0)
            return null;

        //Index is bigger than maximum, return null.
        if (index.compareTo(getTopLimit()) >= 0)
            return null;

        //Go through each range and see if index falls within. If so, return value.
        for (int i = 0; i < values.size(); i++) {
            if (index.compareTo(values.get(i).limit) < 0)
                return values.get(i).value;
        }

        return null;//This should never be reached
    }

    /**
     * Returns the exclusive maximum limit of the entire range.
     * @return
     */
    public L getTopLimit() {
        if (values.isEmpty())
            return minimum;
        else
            return values.get(values.size() - 1).limit;
    }

    /**
     * Remove the range that this index falls into.
     * @param index
     */
    public void remove(L index) {
        //Index is less than minimum, return.
        if (index.compareTo(minimum) < 0)
            return;

        //Index is bigger than maximum, return.
        if (index.compareTo(getTopLimit()) >= 0)
            return;

        //Go through each range and see if index falls within. If so, remove range.
        for (int i = 0; i < values.size(); i++) {
            if (index.compareTo(values.get(i).limit) < 0) {
                values.remove(i);
                return;
            }
        }
    }

    /**
     * Set the value of the range that the specified index falls into.
     * If the range does not exist, nothing will happen.
     * @param index
     * @param value
     */
    public void set(L index, V value) {
        //Index is less than minimum, return.
        if (index.compareTo(minimum) < 0)
            return;

        //Index is bigger than maximum, return.
        if (index.compareTo(getTopLimit()) > 0)
            return;

        //Go through each range and see if index falls within. If so, set value.
        for (int i = 0; i < values.size(); i++) {
            if (index.compareTo(values.get(i).limit) < 0) {
                values.get(i).value = value;
                return;
            }
        }
    }

    /**
     * Set the minimum of the range to a new value.
     * Must be lower than the first defined range's limit.
     * @param minimum
     */
    public void setMinimum(L minimum) {
        if (values.isEmpty() || minimum.compareTo(values.get(0).limit) < 0)
            this.minimum = minimum;
        else
            throw new IllegalRangeMinimumException();
    }

    /**
     * Clears all range definitions from the map.
     */
    public void clear() {
        values.clear();
    }

    private class RangeDefinition {

        public L limit;
        public V value;

        public RangeDefinition(L limit, V value) {
            this.limit = limit;
            this.value = value;
        }

    }

}
