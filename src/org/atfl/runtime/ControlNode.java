package org.atfl.runtime;

import java.util.ArrayList;

public class ControlNode {
    private ArrayList<ControlNode> next;
    private Object value = null;
    private OpCode instr = null;
    private Type type;

    public enum Type {
        INSTR,
        NUM,
        STR,
        LIST
    }

    public static enum OpCode {
        LD,   // load
        LDC,  // load constant
        LDF,  // load function
        AP,   // apply function
        RTN,  // return
        DUM,  // create dummy environment
        RAP,  // recursive apply
        SEL,  // select subcontrol
        JOIN, // rejoin main control
        CAR,  // take car of item on top of stack
        CDR,  // take cdr of item on top of stack
        ATOM, // apply atom predicate to top stack item
        CONS, // form cons of top two stack items
        EQ,   // apply eq predicate to top two stack items
        ADD,  // add
        SUB,  // subtract
        MUL,  // multiply
        DIV,  // divide
        REM,  // remainder
        POW,  // power (raise number to)
        LEQ,  // less than
        STOP  // stop
    }

    public ControlNode(Type tag, OpCode builtInFunc) {
        next = new ArrayList<ControlNode>();
        this.type = tag;
        this.instr = builtInFunc;
    }

    public ControlNode(Type tag, Object value) {
        next = new ArrayList<ControlNode>();
        this.type = tag;
        this.value = value;
    }

    public ArrayList<ControlNode> getSubNodes() { return next; }
    public Type getType() { return type; }
    public OpCode getInstruction() { return instr; }
    public Object getValue() { return value; }
    public void addSubNode(ControlNode n) { next.add(n); }

    @Override
    public String toString() {
        String ret = "";
        ret += type.toString() + ":";
        if (type.equals(Type.INSTR)) ret += instr.toString();
        else ret += value.toString();

        return ret;
    }
    
    public void print() {
        printHelper(0);
        System.out.println();
    }
    
    public void printHelper(int indent) {
        System.out.print("[" + type);
        if (instr != null) {
            System.out.print("(" + instr + ")");
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
