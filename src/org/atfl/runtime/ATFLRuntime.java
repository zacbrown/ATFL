/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.atfl.runtime;
import java.io.FileNotFoundException;
import java.util.Stack;
import org.atfl.exception.ATFLRuntimeException;
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

        reg_c.push(new ControlNode(Type.INSTR, OpCode.STOP));
        reg_c.push(new ControlNode(Type.INSTR, OpCode.ADD));
        reg_c.push(new ControlNode(Type.NUM, new Double(3)));
        reg_c.push(new ControlNode(Type.INSTR, OpCode.LDC));
        reg_c.push(new ControlNode(Type.NUM, new Double(8)));
        reg_c.push(new ControlNode(Type.INSTR, OpCode.LDC));
        dumpRegisters();
        this.execute();
    }

    public ATFLRuntime(Stack<ControlNode> reg_s, Stack<ControlNode> reg_e,
            Stack<ControlNode> reg_c, Stack<ControlNode> reg_d) {
            this.reg_s = reg_s;
            this.reg_e = reg_e;
            this.reg_c = reg_c;
            this.reg_d = reg_d;

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
                try { n.exec(this); }
                catch (ATFLRuntimeException ex) { ex.printStackTrace(); }
            }
        }
    }

    public void dumpRegisters() {
        System.out.println("S:" + reg_s);
        System.out.println("E:" + reg_e);
        System.out.println("C:" + reg_c);
        System.out.println("D:" + reg_d);
    }

    public void pushStack(ControlNode n) { reg_s.push(n); }
    public void pushEnv(ControlNode n) { reg_e.push(n); }
    public void pushControl(ControlNode n) { reg_c.push(n); }
    public void pushDump(ControlNode n) { reg_d.push(n); }
    public ControlNode popStack() { return reg_s.pop(); }
    public ControlNode popEnv() { return reg_e.pop(); }
    public ControlNode popControl() { return reg_c.pop(); }
    public ControlNode popDump() { return reg_d.pop(); }

}