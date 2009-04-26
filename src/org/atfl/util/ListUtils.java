package org.atfl.util;

import java.util.Vector;

public class ListUtils {
    public static Node cons(Node n, Node m) {
        n.getSubNodes().add(0, m);
        return n;
    }

    public static Node car(Node n) {
        Vector next = n.getSubNodes();
        return (Node)next.get(0);
    }

    public static Vector<Node> cdr(Node n) {
        Vector next = n.getSubNodes();
        return new Vector(next.subList(1, next.size()));
    }

    public static Node cadr(Node n) {
        return (Node)cdr(n).get(0);
    }

    public static Vector<Node> cddr(Node n) {
        Vector m = cdr(n);
        return new Vector(m.subList(1, m.size()));
    }

    public static Vector<Node> cdar(Node n) {
        return cdr(car(n));
    }

    public static Node caar(Node n) {
        return car(car(n));
    }

    public static Node caddr(Node n) {
        return cddr(n).get(0);
    }

    public static Node cadddr(Node n) {
        return cddr(n).get(1);
    }
}
