package org.atfl.util;

import java.util.HashMap;
import org.atfl.exception.SymbolException;
import org.atfl.runtime.ControlNode;

public class SymbolTable {
    private HashMap<ControlNode, ControlNode> symbols;
    private ControlNode associatedBlock;
    private SymbolTable parent;

    public SymbolTable(ControlNode associatedBlock, SymbolTable parent) {
        this.associatedBlock = associatedBlock;
        this.parent = parent;
        symbols = new HashMap<ControlNode, ControlNode>();
    }

    public void add(ControlNode n, ControlNode value) throws SymbolException {
        if (symbols.containsKey(n)) {
            throw new SymbolException("Cannot redefine symbol '" + n.toString() + "'");
        }

        symbols.put(n, value);
    }

    public ControlNode getAssociatedBlock() { return associatedBlock; }
    public SymbolTable getParent() { return parent; }
    public ControlNode get(ControlNode n) { return symbols.get(n); }
}
