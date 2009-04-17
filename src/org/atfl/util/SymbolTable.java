package org.atfl.util;

import java.util.HashMap;
import java.util.Stack;

public class SymbolTable {
    private HashMap<String, Stack<Symbol>> symbols;

    public SymbolTable() {
        symbols = new HashMap<String, Stack<Symbol>>();
    }

    public void add(String ref, Symbol.Type type, Object value) {
        /* we've already got one of these, push onto our stack */
        if (symbols.containsKey(ref)) {
            Stack<Symbol> cur_elems = symbols.get(ref);
            cur_elems.push(new Symbol(type, value));
        }
        else {
            Stack<Symbol> new_stack = new Stack<Symbol>();
            new_stack.push(new Symbol(type, value));
            symbols.put(ref, new_stack);
        }
    }

    public Symbol get(String ref) {
        if (symbols.containsKey(ref)) {
            return symbols.get(ref).peek();
        }

        return null;
    }

    public Symbol remove(String ref) {
        if (symbols.containsKey(ref)) {
            Stack<Symbol> cur_elems = symbols.get(ref);
            if (cur_elems.size() == 1) {
                Symbol ret = cur_elems.peek();
                symbols.remove(ref);
                return ret;
            }
            else {
                return cur_elems.pop();
            }
        }
        /* if this happens, its probably an error */
        return null;
    }

}
