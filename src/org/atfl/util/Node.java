package org.atfl.util;

import java.util.Vector;

public abstract class Node {
    private Vector next;

    public void cons(Node n) {
        next.add(0, n);
    }

    public Node car() {
        return (Node)next.get(0);
    }

    public Vector<Node> cdr() {
        return new Vector(next.subList(1, next.size()));
    }

    public Node cadr() {
        return (Node)cdr().get(0);
    }

    public Vector<Node> cddr() {
        return new Vector(cdr().subList(1, next.size()));
    }

    public Vector<Node> cdar() {
        return car().cdr();
    }

    public Node caar() {
        return car().car();
    }

    public Node caddr() {
        return cddr().get(0);
    }

    public Node cadddr() {
        return cddr().subList(1, next.size()).get(0);
    }
}
