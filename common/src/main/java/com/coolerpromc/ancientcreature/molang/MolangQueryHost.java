package com.coolerpromc.ancientcreature.molang;

/**
 * Supplies the values of {@code query.*} for whatever a Molang expression is running against.
 *
 * <p>Names arrive lower-case and without the {@code query.}/{@code q.} prefix. An unknown query must
 * return {@link Double#NaN} from {@link #queryNumber}; the runtime turns that into zero and, the first
 * time it happens for a name, logs it so a typo is visible rather than silently dead.
 */
public interface MolangQueryHost {
    MolangQueryHost NONE = (name, args) -> Double.NaN;

    double queryNumber(String name, MolangArgs args);

    /**
     * A query that may produce a string, for {@code ==}/{@code !=} comparisons. Hosts with string
     * queries override this; the default reports the numeric value.
     */
    default Object queryObject(String name, MolangArgs args) {
        return this.queryNumber(name, args);
    }
}
