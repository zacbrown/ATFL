package org.atfl.util;

import java.util.HashMap;
import org.atfl.exception.SymbolException;
import org.atfl.runtime.ControlNode;

public class SymbolTable {
    private HashMap<String, ControlNode> symbols;
    private ControlNode associatedBlock = null;
    private SymbolTable parent = null;

    public SymbolTable() {
        symbols = new HashMap<String, ControlNode>();
    }

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
        symbols = new HashMap<String, ControlNode>();
    }

    public SymbolTable(ControlNode associatedBlock, SymbolTable parent) {
        this.associatedBlock = associatedBlock;
        this.parent = parent;
        symbols = new HashMap<String, ControlNode>();
    }

    public void add(String n, ControlNode value) throws SymbolException {
        if (symbols.containsKey(n)) {
            throw new SymbolException("Cannot redefine symbol '" + n.toString() + "'");
        }

        symbols.put(n, value);
    }

    public ControlNode getAssociatedBlock() { return associatedBlock; }
    public SymbolTable getParent() { return parent; }
    public ControlNode get(String n) { return symbols.get(n); }

    @Override
    public String toString() { return symbols.toString(); }
}
