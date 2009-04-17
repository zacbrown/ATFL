/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.atfl.runtime;
import java.io.FileNotFoundException;
import java.util.Stack;
import org.atfl.runtime.ControlNode.OpCode;
import org.atfl.runtime.ControlNode.Type;
import org.atfl.runtime.parser.ATFLParser;
import org.atfl.runtime.parser.Node;
import org.atfl.util.TokenReader;

/**
 *
 * @author Zac Brown <zac@zacbrown.org>
 */
public class ATFLRuntime {

    private Stack<ControlNode> reg_s;
    private Stack<ControlNode> reg_e;
    private Stack<ControlNode> reg_c;
    private Stack<ControlNode> reg_d;

    public ATFLRuntime(String filename) {
        reg_s = new Stack<ControlNode>();
        reg_e = new Stack<ControlNode>();
        reg_c = new Stack<ControlNode>();
        reg_d = new Stack<ControlNode>();

        try {
            TokenReader tok = new TokenReader(filename);
            ATFLParser parser = new ATFLParser(tok);
            try { parser.parse(); }
            catch (Exception ex) { ex.printStackTrace(); }
            Node my_node = parser.getAST();
            //my_node.print();
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }

        this.execute();
    }

    public static void main(String args[]) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar /path/to/ATFL.jar <script name>");
            return;
        }
        
        new ATFLRuntime(args[0]);
    }

    private void execute() {
        while(reg_c.isEmpty() == false) {
            ControlNode n = reg_c.pop();
            Type n_type = n.getType();

            if (n_type == Type.INSTR) {
                OpCode instr = n.getInstruction();

                if (instr.equals(OpCode.SEL)) { instrSEL(); }
                if (instr.equals(OpCode.JOIN)) { instrJOIN(); }
                if (instr.equals(OpCode.ADD)) { instrADD(); }
                if (instr.equals(OpCode.SUB)) { instrSUB(); }
                if (instr.equals(OpCode.MUL)) { instrMUL(); }
                if (instr.equals(OpCode.DIV)) { instrDIV(); }
                if (instr.equals(OpCode.POW)) { instrPOW(); }
                if (instr.equals(OpCode.REM)) { instrREM(); }
                if (instr.equals(OpCode.EQ)) { instrEQ(); }
                if (instr.equals(OpCode.LEQ)) { instrLEQ(); }
                if (instr.equals(OpCode.ATOM)) { instrATOM(); }
                if (instr.equals(OpCode.AP)) { instrAP(); }
                if (instr.equals(OpCode.RAP)) { instrRAP(); }
                if (instr.equals(OpCode.LD)) { instrLD(); }
                if (instr.equals(OpCode.LDC)) { instrLDC(); }
                if (instr.equals(OpCode.LDF)) { instrLDF(); }
                if (instr.equals(OpCode.RTN)) { instrRTN(); }
                if (instr.equals(OpCode.DUM)) { instrDUM(); }
                if (instr.equals(OpCode.CONS)) { instrCONS(); }
                if (instr.equals(OpCode.CAR)) { instrCAR(); }
                if (instr.equals(OpCode.CDR)) { instrCDR(); }
                if (instr.equals(OpCode.STOP)) { break; }
            }
        }
    }

    public void dumpRegisters() {
        System.out.println("S:" + reg_s);
        System.out.println("E:" + reg_e);
        System.out.println("C:" + reg_c);
        System.out.println("D:" + reg_d);
    }

    private void instrSEL() {

    }

    private void instrJOIN() {

    }

    private void instrADD() {
        ControlNode op_1 = reg_s.pop();
        ControlNode op_2 = reg_s.pop();
        Type t_1 = op_1.getType();
        Type t_2 = op_2.getType();
        if (!t_1.equals(Type.NUM) || !t_2.equals(Type.NUM)) {
            throw new ArithmeticException("Addition must be performed on type NUM: " +
                    op_2 + " + " + op_1);
        }

        double val_1 = ((Double)op_1.getValue()).doubleValue();
        double val_2 = ((Double)op_2.getValue()).doubleValue();

 
        reg_s.push(new ControlNode(Type.NUM, new Double(val_2 + val_1)));
    }

    private void instrSUB() {
        ControlNode op_1 = reg_s.pop();
        ControlNode op_2 = reg_s.pop();
        Type t_1 = op_1.getType();
        Type t_2 = op_2.getType();
        if (!t_1.equals(Type.NUM) || !t_2.equals(Type.NUM)) {
            throw new ArithmeticException("Subtraction must be performed on type NUM: " +
                    op_2 + " - " + op_1);
        }

        double val_1 = ((Double)op_1.getValue()).doubleValue();
        double val_2 = ((Double)op_2.getValue()).doubleValue();


        reg_s.push(new ControlNode(Type.NUM, new Double(val_2 - val_1)));
    }

    private void instrMUL() {
        ControlNode op_1 = reg_s.pop();
        ControlNode op_2 = reg_s.pop();
        Type t_1 = op_1.getType();
        Type t_2 = op_2.getType();
        if (!t_1.equals(Type.NUM) || !t_2.equals(Type.NUM)) {
            throw new ArithmeticException("Multiplication must be performed on type NUM: " +
                    op_2 + " * " + op_1);
        }

        double val_1 = ((Double)op_1.getValue()).doubleValue();
        double val_2 = ((Double)op_2.getValue()).doubleValue();


        reg_s.push(new ControlNode(Type.NUM, new Double(val_2 * val_1)));
    }

    private void instrDIV() {
        ControlNode op_1 = reg_s.pop();
        ControlNode op_2 = reg_s.pop();
        Type t_1 = op_1.getType();
        Type t_2 = op_2.getType();
        if (!t_1.equals(Type.NUM) || !t_2.equals(Type.NUM)) {
            throw new ArithmeticException("Division must be performed on type NUM: " +
                    op_2 + " / " + op_1);
        }

        double val_1 = ((Double)op_1.getValue()).doubleValue();
        double val_2 = ((Double)op_2.getValue()).doubleValue();


        reg_s.push(new ControlNode(Type.NUM, new Double(val_2 / val_1)));
    }

    private void instrPOW() {
        ControlNode op_1 = reg_s.pop();
        ControlNode op_2 = reg_s.pop();
        Type t_1 = op_1.getType();
        Type t_2 = op_2.getType();
        if (!t_1.equals(Type.NUM) || !t_2.equals(Type.NUM)) {
            throw new ArithmeticException("Exponentiation must be performed on type NUM: " +
                    op_2 + " ** " + op_1);
        }

        double val_1 = ((Double)op_1.getValue()).doubleValue();
        double val_2 = ((Double)op_2.getValue()).doubleValue();


        reg_s.push(new ControlNode(Type.NUM, new Double(Math.pow(val_2, val_1))));
    }

    private void instrREM() {
        ControlNode op_1 = reg_s.pop();
        ControlNode op_2 = reg_s.pop();
        Type t_1 = op_1.getType();
        Type t_2 = op_2.getType();
        if (!t_1.equals(Type.NUM) || !t_2.equals(Type.NUM)) {
            throw new ArithmeticException("Modulo must be performed on type NUM: " +
                    op_2 + " % " + op_1);
        }

        double val_1 = ((Double)op_1.getValue()).doubleValue();
        double val_2 = ((Double)op_2.getValue()).doubleValue();


        reg_s.push(new ControlNode(Type.NUM, new Double(val_2 % val_1)));
    }

    private void instrEQ() {

    }

    private void instrLEQ() {

    }

    private void instrATOM() {
        
    }

    private void instrAP() {

    }

    private void instrRAP() {

    }

    private void instrLD() {

    }

    private void instrLDC() {
        reg_s.push(reg_c.pop());
    }

    private void instrLDF() {

    }

    private void instrRTN() {

    }

    private void instrDUM() {

    }

    private void instrCONS() {

    }

    private void instrCAR() {

    }

    private void instrCDR() {

    }
}
