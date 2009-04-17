package org.atfl.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import org.atfl.exception.ATFLRuntimeException;

public class ControlNode {
    private ArrayList<ControlNode> next;
    private Object value = null;
    private OpCode instr = null;
    private Type type;
    private static HashMap<OpCode, Instruction> instructionTable =
            new HashMap<OpCode, Instruction>();

    static {
        instructionTable.put(OpCode.ADD, new ADD());
        instructionTable.put(OpCode.SUB, new SUB());
        instructionTable.put(OpCode.MUL, new MUL());
        instructionTable.put(OpCode.DIV, new DIV());
        instructionTable.put(OpCode.POW, new POW());
        instructionTable.put(OpCode.REM, new REM());
        instructionTable.put(OpCode.LDC, new LDC());
        instructionTable.put(OpCode.STOP, new STOP());
    }

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

    public void exec(ATFLRuntime runtime) throws ATFLRuntimeException {
        Instruction op = instructionTable.get(instr);
        if (op == null)
            throw new ATFLRuntimeException("Unknown instruction encountered '"
                    + instr + "'");
        op.exec(runtime);
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

    private interface Instruction {
        public void exec(ATFLRuntime runtime);
    }

    private static class ADD implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode op_1 = runtime.popStack();
            ControlNode op_2 = runtime.popStack();
            Type t_1 = op_1.getType();
            Type t_2 = op_2.getType();
            if (!t_1.equals(Type.NUM) || !t_2.equals(Type.NUM)) {
                throw new ArithmeticException("Addition must be performed on type NUM: " +
                        op_2 + " + " + op_1);
            }

            double val_1 = ((Double)op_1.getValue()).doubleValue();
            double val_2 = ((Double)op_2.getValue()).doubleValue();

            runtime.pushStack(new ControlNode(Type.NUM, new Double(val_2 + val_1)));
        }
    }

    private static class SUB implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode op_1 = runtime.popStack();
            ControlNode op_2 = runtime.popStack();
            Type t_1 = op_1.getType();
            Type t_2 = op_2.getType();
            if (!t_1.equals(Type.NUM) || !t_2.equals(Type.NUM)) {
                throw new ArithmeticException("Subtraction must be performed on type NUM: " +
                        op_2 + " - " + op_1);
            }

            double val_1 = ((Double) op_1.getValue()).doubleValue();
            double val_2 = ((Double) op_2.getValue()).doubleValue();

            runtime.pushStack(new ControlNode(Type.NUM, new Double(val_2 - val_1)));
        }
    }

    private static class MUL implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode op_1 = runtime.popStack();
            ControlNode op_2 = runtime.popStack();
            Type t_1 = op_1.getType();
            Type t_2 = op_2.getType();
            if (!t_1.equals(Type.NUM) || !t_2.equals(Type.NUM)) {
                throw new ArithmeticException("Addition must be performed on type NUM: " +
                        op_2 + " + " + op_1);
            }

            double val_1 = ((Double) op_1.getValue()).doubleValue();
            double val_2 = ((Double) op_2.getValue()).doubleValue();

            runtime.pushStack(new ControlNode(Type.NUM, new Double(val_2 * val_1)));
        }
    }

    public static class DIV implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode op_1 = runtime.popStack();
            ControlNode op_2 = runtime.popStack();
            Type t_1 = op_1.getType();
            Type t_2 = op_2.getType();
            if (!t_1.equals(Type.NUM) || !t_2.equals(Type.NUM)) {
                throw new ArithmeticException("Addition must be performed on type NUM: " +
                        op_2 + " + " + op_1);
            }

            double val_1 = ((Double) op_1.getValue()).doubleValue();
            double val_2 = ((Double) op_2.getValue()).doubleValue();

            runtime.pushStack(new ControlNode(Type.NUM, new Double(val_2 / val_1)));
        }
    }

    public static class POW implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode op_1 = runtime.popStack();
            ControlNode op_2 = runtime.popStack();
            Type t_1 = op_1.getType();
            Type t_2 = op_2.getType();
            if (!t_1.equals(Type.NUM) || !t_2.equals(Type.NUM)) {
                throw new ArithmeticException("Exponentiation must be performed on type NUM: " +
                        op_2 + " ** " + op_1);
            }

            double val_1 = ((Double) op_1.getValue()).doubleValue();
            double val_2 = ((Double) op_2.getValue()).doubleValue();

            runtime.pushStack(new ControlNode(Type.NUM, new Double(Math.pow(val_2, val_1))));
        }
    }

    public static class REM implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode op_1 = runtime.popStack();
            ControlNode op_2 = runtime.popStack();
            Type t_1 = op_1.getType();
            Type t_2 = op_2.getType();
            if (!t_1.equals(Type.NUM) || !t_2.equals(Type.NUM)) {
                throw new ArithmeticException("Modulo must be performed on type NUM: " +
                        op_2 + " % " + op_1);
            }

            double val_1 = ((Double) op_1.getValue()).doubleValue();
            double val_2 = ((Double) op_2.getValue()).doubleValue();

            runtime.pushStack(new ControlNode(Type.NUM, new Double(val_2 % val_1)));
        }
    }

    public static class LDC implements Instruction {
        public void exec(ATFLRuntime runtime) {
            runtime.pushStack(runtime.popControl());
        }
    }

    public static class STOP implements Instruction {
        public void exec(ATFLRuntime runtime) {
            runtime.dumpRegisters();
            System.exit(0);
        }
    }
}
