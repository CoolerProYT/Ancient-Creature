package com.coolerpromc.ancientcreature.molang;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SplittableRandom;

/**
 * Everything an expression can read or write while it runs.
 *
 * <p>One context normally belongs to one entity: {@link #variables} is that entity's persistent
 * {@code variable.*} storage, exactly as in Bedrock. {@code temp.*} is cleared before every top-level
 * evaluation; {@code context.*} is set by whoever invokes the expression. A context is not thread-safe
 * and is meant to be used from the render thread only.
 */
public final class MolangContext {
    private static final Set<String> WARNED_QUERIES = Collections.synchronizedSet(new HashSet<>());

    public final MolangVariables variables;
    public final MolangVariables temp = new MolangVariables();
    public final MolangVariables context = new MolangVariables();

    private MolangQueryHost host;
    private MolangResources resources = MolangResources.NONE;
    private SplittableRandom random = new SplittableRandom();

    /** The value of {@code this}: for a keyframe, the channel's value before this keyframe applied. */
    public double thisValue;

    // control flow for complex expressions
    boolean returned;
    Object returnValue = 0.0;
    boolean breaking;
    boolean continuing;

    public MolangContext() {
        this(new MolangVariables(), MolangQueryHost.NONE);
    }

    public MolangContext(MolangVariables variables, MolangQueryHost host) {
        this.variables = variables;
        this.host = host;
    }

    public MolangQueryHost host() {
        return this.host;
    }

    public MolangContext host(MolangQueryHost host) {
        this.host = host == null ? MolangQueryHost.NONE : host;
        return this;
    }

    public MolangResources resources() {
        return this.resources;
    }

    public MolangContext resources(MolangResources resources) {
        this.resources = resources == null ? MolangResources.NONE : resources;
        return this;
    }

    public MolangContext seed(long seed) {
        this.random = new SplittableRandom(seed);
        return this;
    }

    double random() {
        return this.random.nextDouble();
    }

    void beginEvaluation() {
        this.temp.clear();
        this.returned = false;
        this.returnValue = 0.0;
        this.breaking = false;
        this.continuing = false;
    }

    double queryNumber(String name, MolangArgs args) {
        double value = this.host.queryNumber(name, args);
        if (Double.isNaN(value)) {
            warnUnknown(name);
            return 0.0;
        }
        return value;
    }

    Object queryObject(String name, MolangArgs args) {
        Object value = this.host.queryObject(name, args);
        if (value instanceof Double d && d.isNaN()) {
            warnUnknown(name);
            return 0.0;
        }
        return value == null ? 0.0 : value;
    }

    private static void warnUnknown(String name) {
        if (WARNED_QUERIES.add(name)) {
            Molang.LOG.warn("Molang query 'query.{}' is not supported by Ancient Creature; it evaluates to 0.", name);
        }
    }
}
