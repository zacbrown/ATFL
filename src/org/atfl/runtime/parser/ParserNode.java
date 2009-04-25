package org.atfl.runtime.parser;

import java.util.Vector;

public class ParserNode {
    private Vector<ParserNode> next;
    private Object value = null;
    private NodeTag tag;

    public enum NodeTag {
        ATOM,
        LIST,
    }

    public ParserNode(NodeTag tag) {
        next = new Vector<ParserNode>();
        this.tag = tag;
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
    public Object getValue() { return value; }
    public Vector<ParserNode> getSubNodes() { return next; }
    public void addSubNode(ParserNode n) { next.add(n); }

    public void print() {
        printHelper(0);
        System.out.println();
    }
    
    public void printHelper(int indent) {
        System.out.print("[" + tag);
        if (value != null) {
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
