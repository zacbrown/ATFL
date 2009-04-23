package org.atfl.runtime.parser;

import java.util.Vector;

public class ParserNode {
    private Vector<ParserNode> next;
    private Object value = null;
    private BuiltIn funCall = null;
    private NodeTag tag;

    public enum NodeTag {
        BUILTIN,
        ATOM,
        LIST,
        TOP
    }

    public static enum BuiltIn {
        FUN,   // define function
        SET,   // set value to a label
        CAR,   // take car of item on top of stack
        CDR,   // take cdr of item on top of stack
        ATOM,  // apply atom predicate to top stack item
        LIST,  // create list from following items
        CONS,  // form cons of top two stack items
        EQ,    // apply eq predicate to top two stack items
        ADD,   // add
        SUB,   // subtract
        MUL,   // multiply
        DIV,   // divide
        REM,   // remainder
        POW,   // power (raise number to)
        LEQ,   // less than
        IF,    // if/else statement
        PRINT, // print
        STOP   // stop
    }

    public ParserNode(NodeTag tag) {
        next = new Vector<ParserNode>();
        this.tag = tag;
    }

    public ParserNode(NodeTag tag, BuiltIn builtInFunc) {
        next = new Vector<ParserNode>();
        this.tag = tag;
        this.funCall = builtInFunc;
    }

    public ParserNode(NodeTag tag, Object value) {
        next = new Vector<ParserNode>();
        this.tag = tag;
        this.value = value;
    }

    public ParserNode(NodeTag tag, ParserNode subNode) {
        next = new Vector<ParserNode>();
        addSubNode(subNode);
        this.tag = tag;
    }

    public NodeTag getTag() { return tag; }
    public BuiltIn getBuiltInFunction() { return funCall; }
    public Object getValue() { return value; }
    public Vector<ParserNode> getSubNodes() { return next; }
    public void addSubNode(ParserNode n) { next.add(n); }

    public void print() {
        printHelper(0);
        System.out.println();
    }
    
    public void printHelper(int indent) {
        System.out.print("[" + tag);
        if (funCall != null) {
            System.out.print("(" + funCall + ")");
        } else if (value != null) {
            System.out.print(":" + value.toString());
        }
        indent += 6;

        for (int i=0; i < next.size(); i+=1) {
            System.out.print(",\n");
            for (int j=0; j<indent; j++)
                System.out.print(" ");
            next.get(i).printHelper(indent + 2);
        }
        System.out.print("]");
    }

}
