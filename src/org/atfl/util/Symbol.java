package org.atfl.util;

public class Symbol {
    public static enum Type { INT, DOUBLE, STRING, FUNCTION, LIST, MAP };

    private Type type;
    private Object value;

    public Symbol(Type type, Object value) {
        this.value = value;
        this.type = type;
    }

    public Type getType() { return type; }
    public Object getValue() { return value; }
}
