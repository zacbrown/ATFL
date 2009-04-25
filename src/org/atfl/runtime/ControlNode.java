package org.atfl.runtime;

import java.util.Vector;
import java.util.HashMap;
import java.util.Stack;
import org.atfl.exception.ATFLRuntimeException;
import org.atfl.util.Node;
import org.atfl.util.SymbolTable;

public class ControlNode extends Node {
    private Vector<ControlNode> next;
    private Stack<SymbolTable> env = null;
    private Object value = null;
    private OpCode instr = null;
    private String[] funcArgs = null;
    private ControlNodeTag type;
    private static HashMap<OpCode, Instruction> instructionTable =
            new HashMap<OpCode, Instruction>();

    static {
        instructionTable.put(OpCode.ADD, new ADD());
        instructionTable.put(OpCode.SUB, new SUB());
        instructionTable.put(OpCode.MUL, new MUL());
        instructionTable.put(OpCode.DIV, new DIV());
        instructionTable.put(OpCode.POW, new POW());
        instructionTable.put(OpCode.REM, new REM());
        instructionTable.put(OpCode.PRINT, new PRINT());
        instructionTable.put(OpCode.LDC, new LDC());
        instructionTable.put(OpCode.LD, new LD());
        instructionTable.put(OpCode.LDF, new LDF());
        instructionTable.put(OpCode.AP, new AP());
        instructionTable.put(OpCode.SEL, new SEL());
        instructionTable.put(OpCode.JOIN, new JOIN());
        instructionTable.put(OpCode.CAR, new CAR());
        instructionTable.put(OpCode.CDR, new CDR());
        instructionTable.put(OpCode.CONS, new CONS());
        instructionTable.put(OpCode.EQ, new EQ());
        instructionTable.put(OpCode.LEQ, new LEQ());
        instructionTable.put(OpCode.ATOM, new ATOM());
        instructionTable.put(OpCode.RTN, new RTN());
        instructionTable.put(OpCode.STOP, new STOP());
    }

    public enum ControlNodeTag {
        INSTR,
        NUM,
        STR,
        BOOL,
        SYMBOL,
        NIL,
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
        PRINT,// print top item on stack
        STOP  // stop
    }

    public ControlNode(ControlNodeTag tag) {
        next = new Vector<ControlNode>();
        this.type = tag;
    }

    public ControlNode(ControlNodeTag tag, OpCode builtInFunc) {
        next = new Vector<ControlNode>();
        this.type = tag;
        this.instr = builtInFunc;
    }

    public ControlNode(ControlNodeTag tag, Object value) {
        next = new Vector<ControlNode>();
        this.type = tag;
        this.value = value;
    }

    public ControlNode(ControlNodeTag tag, Vector<ControlNode> next) {
        this.next = next;
        this.type = tag;
    }

    public void exec(ATFLRuntime runtime) throws ATFLRuntimeException {
        Instruction op = instructionTable.get(instr);
        if (op == null)
            throw new ATFLRuntimeException("Unknown instruction encountered '"
                    + instr + "'");
        op.exec(runtime);
    }

    public void setFuncArgs(String[] funcArgs) { this.funcArgs = funcArgs; }
    public String[] getFuncArgs() { return funcArgs; }
    public void setEnv(Stack<SymbolTable> env) { this.env = env; }
    public Stack<SymbolTable> getEnv() { return env; }
    public Vector<ControlNode> getSubNodes() { return next; }
    public ControlNodeTag getType() { return type; }
    public OpCode getInstruction() { return instr; }
    public Object getValue() { return value; }
    public void addSubNode(ControlNode n) { next.add(n); }

    @Override
    public String toString() {
        String ret = "";
        ret += type.toString() + ":";
        if (type.equals(ControlNodeTag.INSTR)) ret += instr.toString();
        else if (type.equals(ControlNodeTag.LIST)) {
            if (next.size() != 0) {
                ret += "(";
                for (int i = 0; i < next.size()-1; i++) {
                    ret += next.get(i).toString() + ",";
                }
                ret += next.get(next.size() - 1).toString();
                ret += ")";
            }
            else { ret += "()"; }
        }
        else if (value != null) {
            ret += value.toString();
        }

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
            ControlNodeTag t_1 = op_1.getType();
            ControlNodeTag t_2 = op_2.getType();
            if (!t_1.equals(ControlNodeTag.NUM) || !t_2.equals(ControlNodeTag.NUM)) {
                throw new ArithmeticException("Addition must be performed on type NUM: " +
                        op_2 + " + " + op_1);
            }

            double val_1 = ((Double)op_1.getValue()).doubleValue();
            double val_2 = ((Double)op_2.getValue()).doubleValue();

            runtime.pushStack(new ControlNode(ControlNodeTag.NUM, new Double(val_2 + val_1)));
        }
    }

    private static class SUB implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode op_1 = runtime.popStack();
            ControlNode op_2 = runtime.popStack();
            ControlNodeTag t_1 = op_1.getType();
            ControlNodeTag t_2 = op_2.getType();
            if (!t_1.equals(ControlNodeTag.NUM) || !t_2.equals(ControlNodeTag.NUM)) {
                throw new ArithmeticException("Subtraction must be performed on type NUM: " +
                        op_2 + " - " + op_1);
            }

            double val_1 = ((Double) op_1.getValue()).doubleValue();
            double val_2 = ((Double) op_2.getValue()).doubleValue();

            runtime.pushStack(new ControlNode(ControlNodeTag.NUM, new Double(val_2 - val_1)));
        }
    }

    private static class MUL implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode op_1 = runtime.popStack();
            ControlNode op_2 = runtime.popStack();
            ControlNodeTag t_1 = op_1.getType();
            ControlNodeTag t_2 = op_2.getType();
            if (!t_1.equals(ControlNodeTag.NUM) || !t_2.equals(ControlNodeTag.NUM)) {
                throw new ArithmeticException("Addition must be performed on type NUM: " +
                        op_2 + " + " + op_1);
            }

            double val_1 = ((Double) op_1.getValue()).doubleValue();
            double val_2 = ((Double) op_2.getValue()).doubleValue();

            runtime.pushStack(new ControlNode(ControlNodeTag.NUM, new Double(val_2 * val_1)));
        }
    }

    private static class DIV implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode op_1 = runtime.popStack();
            ControlNode op_2 = runtime.popStack();
            ControlNodeTag t_1 = op_1.getType();
            ControlNodeTag t_2 = op_2.getType();
            if (!t_1.equals(ControlNodeTag.NUM) || !t_2.equals(ControlNodeTag.NUM)) {
                throw new ArithmeticException("Addition must be performed on type NUM: " +
                        op_2 + " + " + op_1);
            }

            double val_1 = ((Double) op_1.getValue()).doubleValue();
            double val_2 = ((Double) op_2.getValue()).doubleValue();

            runtime.pushStack(new ControlNode(ControlNodeTag.NUM, new Double(val_2 / val_1)));
        }
    }

    private static class POW implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode op_1 = runtime.popStack();
            ControlNode op_2 = runtime.popStack();
            ControlNodeTag t_1 = op_1.getType();
            ControlNodeTag t_2 = op_2.getType();
            if (!t_1.equals(ControlNodeTag.NUM) || !t_2.equals(ControlNodeTag.NUM)) {
                throw new ArithmeticException("Exponentiation must be performed on type NUM: " +
                        op_2 + " ** " + op_1);
            }

            double val_1 = ((Double) op_1.getValue()).doubleValue();
            double val_2 = ((Double) op_2.getValue()).doubleValue();

            runtime.pushStack(new ControlNode(ControlNodeTag.NUM, new Double(Math.pow(val_2, val_1))));
        }
    }

    private static class REM implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode op_1 = runtime.popStack();
            ControlNode op_2 = runtime.popStack();
            ControlNodeTag t_1 = op_1.getType();
            ControlNodeTag t_2 = op_2.getType();
            if (!t_1.equals(ControlNodeTag.NUM) || !t_2.equals(ControlNodeTag.NUM)) {
                throw new ArithmeticException("Modulo must be performed on type NUM: " +
                        op_2 + " % " + op_1);
            }

            double val_1 = ((Double) op_1.getValue()).doubleValue();
            double val_2 = ((Double) op_2.getValue()).doubleValue();

            runtime.pushStack(new ControlNode(ControlNodeTag.NUM, new Double(val_2 % val_1)));
        }
    }

    private static class PRINT implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode n = runtime.popStack();
            SymbolTable env = (SymbolTable)runtime.peekEnv();

            if (n.getType().equals(ControlNodeTag.SYMBOL)) {
                n = (ControlNode)env.get((String)n.getValue());
            }

            System.out.println(n.getValue());
        }
    }

    private static class LD implements Instruction {
        public void exec(ATFLRuntime runtime) {
            SymbolTable curEnv = (SymbolTable)runtime.peekEnv();
            runtime.pushStack(curEnv.get((String)runtime.popControl().getValue()));
        }
    }

    private static class LDC implements Instruction {
        public void exec(ATFLRuntime runtime) {
            runtime.pushStack(runtime.popControl());
        }
    }

    private static class LDF implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode n = runtime.popControl();
            ControlNodeTag nType = n.getType();
            if (nType.equals(ControlNodeTag.SYMBOL)) {
                SymbolTable env = (SymbolTable)runtime.peekEnv();
                n = env.get((String)n.getValue());
            }
            Stack<SymbolTable> oldEnv = runtime.cloneEnv();
            n.setEnv(oldEnv);
            runtime.pushStack(n);
        }
    }
    
    private static class AP implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode newControlList = runtime.popStack();
            Stack<SymbolTable> newEnv = newControlList.getEnv();
            Stack<SymbolTable> oldEnv = runtime.swapEnv(newEnv);
            runtime.pushEnv(runtime.popStack());
            Stack<ControlNode> newControl = new Stack<ControlNode>();
            newControl.addAll(0, newControlList.getSubNodes());
            Stack<ControlNode> oldControl = runtime.swapControl(newControl);
            Stack oldStack = runtime.swapStack(new Stack());
            runtime.pushDump(oldControl);
            runtime.pushDump(oldEnv);
            runtime.pushDump(oldStack);
        }
    }

    private static class SEL implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode val = runtime.popStack();
            ControlNode controlTrue = runtime.popControl();
            ControlNode controlFalse = runtime.popControl();
            Stack<ControlNode> newControl = null;

            if (val.getValue().equals(Boolean.TRUE)) {
                newControl = (Stack<ControlNode>) new Vector(controlTrue.getSubNodes());
            }
            else {
                newControl = (Stack<ControlNode>) new Vector(controlFalse.getSubNodes());
            }

            runtime.pushDump(runtime.swapControl(newControl));
        }
    }

    private static class JOIN implements Instruction {
        public void exec(ATFLRuntime runtime) {
            runtime.swapControl((Stack<ControlNode>)runtime.popDump());
        }
    }

    private static class CAR implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode n = runtime.popStack();
            ControlNodeTag nType = n.getType();
            if (nType.equals(ControlNodeTag.SYMBOL)) {
                SymbolTable env = (SymbolTable)runtime.peekEnv();
                n = env.get((String)n.getValue());
            }
            Vector<ControlNode> nodes = n.getSubNodes();
            runtime.pushStack(nodes.get(0));
        }
    }

    private static class CDR implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode n = runtime.popStack();
            ControlNodeTag nType = n.getType();
            if (nType.equals(ControlNodeTag.SYMBOL)) {
                SymbolTable env = (SymbolTable)runtime.peekEnv();
                n = env.get((String)n.getValue());
            }
            Vector<ControlNode> nodes = n.getSubNodes();
            runtime.pushStack(new ControlNode(ControlNodeTag.LIST,
                    new Vector<ControlNode>(nodes.subList(1, nodes.size()))));
        }
    }

    private static class CONS implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode elem = runtime.popStack();
            ControlNode list = runtime.popStack();
            SymbolTable env = (SymbolTable)runtime.peekEnv();

            if (elem.getType().equals(ControlNodeTag.SYMBOL)) {
                elem = env.get((String)elem.getValue());
            }

            if (elem.getType().equals(ControlNodeTag.SYMBOL)) {
                list = env.get((String)list.getValue());
            }

            list.getSubNodes().add(0, elem);
            runtime.pushStack(list);
        }
    }

    private static class ATOM implements Instruction {
        private static HashMap<ControlNodeTag, Boolean> validAtoms =
                new HashMap<ControlNodeTag, Boolean>();
        static {
            validAtoms.put(ControlNodeTag.NUM, Boolean.TRUE);
            validAtoms.put(ControlNodeTag.STR, Boolean.TRUE);
            validAtoms.put(ControlNodeTag.LIST, Boolean.FALSE);
            validAtoms.put(ControlNodeTag.BOOL, Boolean.TRUE);
            validAtoms.put(ControlNodeTag.NIL, Boolean.TRUE);
        }

        public void exec(ATFLRuntime runtime) {
            ControlNode n = runtime.popStack();
            ControlNodeTag nType = n.getType();
            if (nType.equals(ControlNodeTag.SYMBOL)) {
                SymbolTable env = (SymbolTable)runtime.peekEnv();
                n = env.get((String)n.getValue());
                nType = n.getType();
            }
            Boolean isAtom = validAtoms.get(nType);
            runtime.pushStack(new ControlNode(ControlNodeTag.BOOL, isAtom));
        }
    }

    private static class EQ implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode val_1 = runtime.popStack();
            ControlNode val_2 = runtime.popStack();
            ControlNodeTag val_1_type = val_1.getType();
            ControlNodeTag val_2_type = val_2.getType();
            SymbolTable env = (SymbolTable)runtime.peekEnv();


            if (val_1_type.equals(ControlNodeTag.SYMBOL)) {
                val_1 = env.get((String)val_1.getValue());
                val_1_type = val_1.getType();
            }

            if (val_2_type.equals(ControlNodeTag.SYMBOL)) {
                val_2 = env.get((String)val_1.getValue());
                val_2_type = val_2.getType();
            }

            if (val_1_type.equals(ControlNodeTag.LIST)) {
                runtime.pushStack(new ControlNode(ControlNodeTag.BOOL, equalsList(val_1, val_2)));
            }
            else {
                if (val_1.getValue().equals(val_2.getValue())) {
                    runtime.pushStack(new ControlNode(ControlNodeTag.BOOL, Boolean.TRUE));
                }
                else {
                    runtime.pushStack(new ControlNode(ControlNodeTag.BOOL, Boolean.FALSE));
                }
            }
        }

        private Boolean equalsList(ControlNode v1, ControlNode v2) {
            Vector<ControlNode> n1 = v1.getSubNodes();
            Vector<ControlNode> n2 = v2.getSubNodes();
            int n1_size = n1.size();
            if (n1_size != n2.size()) { return Boolean.FALSE; }

            for (int i = 0; i < n1_size; i++) {
                if (n1.get(i).getValue().equals(n2.get(i).getValue()) != true) {
                    return Boolean.FALSE;
                }
            }

            return Boolean.TRUE;
        }
    }

    private static class LEQ implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode n_1 = runtime.popStack();
            ControlNode n_2 = runtime.popStack();
            Object v_1 = n_1.getValue();
            Object v_2 = n_2.getValue();
            SymbolTable env = (SymbolTable)runtime.peekEnv();

            if (n_1.getType().equals(ControlNodeTag.SYMBOL)) {
                n_1 = env.get((String)v_1);
                v_1 = n_1.getValue();
            }

            if (n_2.getType().equals(ControlNodeTag.SYMBOL)) {
                n_2 = env.get((String)v_1);
                v_2 = n_2.getValue();
            }

            if (((Comparable)v_2).compareTo((Comparable)v_1) == 1) {
                runtime.pushStack(new ControlNode(ControlNodeTag.BOOL, Boolean.FALSE));
            }
            else {
                runtime.pushStack(new ControlNode(ControlNodeTag.BOOL, Boolean.TRUE));
            }
        }
    }

    private static class RTN implements Instruction {
        public void exec(ATFLRuntime runtime) {
            ControlNode retVal = runtime.popStack();

            runtime.swapStack((Stack)runtime.popDump());
            runtime.swapEnv((Stack)runtime.popDump());
            runtime.swapControl((Stack)runtime.popDump());
            runtime.pushStack(retVal);
        }
    }

    private static class STOP implements Instruction {
        public void exec(ATFLRuntime runtime) {
            runtime.dumpRegisters();
            System.exit(0);
        }
    }
}
