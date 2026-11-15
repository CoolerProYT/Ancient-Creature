package com.coolerpromc.ancientcreature.molang;

public final class MolangArgs {
    static final MolangArgs NONE = new MolangArgs(new MolangNode[0], null);

    private final MolangNode[] nodes;
    private final MolangContext context;

    MolangArgs(MolangNode[] nodes, MolangContext context) {
        this.nodes = nodes;
        this.context = context;
    }

    public int size() {
        return this.nodes.length;
    }

    public double number(int index) {
        return index < this.nodes.length ? this.nodes[index].number(this.context) : 0.0;
    }

    public double number(int index, double fallback) {
        return index < this.nodes.length ? this.nodes[index].number(this.context) : fallback;
    }

    public String string(int index) {
        if (index >= this.nodes.length) {
            return "";
        }
        Object value = this.nodes[index].object(this.context);
        return value instanceof String s ? s : MolangValues.toText(value);
    }

    public Object value(int index) {
        return index < this.nodes.length ? this.nodes[index].object(this.context) : 0.0;
    }
}
