/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.atfl.runtime;
import java.io.FileNotFoundException;
import java.util.Stack;
import org.atfl.exception.ATFLRuntimeException;
import org.atfl.runtime.ControlNode.ControlNodeTag;
import org.atfl.runtime.parser.ATFLParser;
import org.atfl.runtime.parser.ParserNode;
import org.atfl.runtime.translator.ATFLTranslator;
import org.atfl.util.SymbolTable;
import org.atfl.util.TokenReader;

/**
 *
 * @author Zac Brown <zac@zacbrown.org>
 */
public class ATFLRuntime {

    private Stack<ControlNode> reg_s;
    private Stack reg_e;
    private Stack<ControlNode> reg_c;
    private Stack reg_d;

    public ATFLRuntime(String filename) {
        reg_s = new Stack<ControlNode>();
        reg_c = new Stack<ControlNode>();
        reg_d = new Stack();

        try {
            TokenReader tok = new TokenReader(filename);
            ATFLParser parser = new ATFLParser(tok);
            try { parser.parse(); }
            catch (Exception ex) { ex.printStackTrace(); }
            ParserNode my_node = parser.getAST();
            //my_node.print();
            ATFLTranslator translator = new ATFLTranslator(my_node);
            try { translator.translateTop(); }
            catch (Exception ex) { ex.printStackTrace(); }
            reg_c.addAll(translator.getControl().getSubNodes());
            reg_e = translator.getEnv();
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }
        this.execute();
    }

    public ATFLRuntime(Stack<ControlNode> reg_s, Stack<SymbolTable> reg_e,
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
            ControlNodeTag n_type = n.getType();

            if (n_type == ControlNodeTag.INSTR) {
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
        System.out.println();
    }

    public Object peekEnv() { return reg_e.peek(); }
    public Stack<SymbolTable> cloneEnv() { return (Stack<SymbolTable>)reg_e.clone(); }
    public void pushStack(ControlNode n) { reg_s.push(n); }
    public void pushEnv(Object t) { reg_e.push(t); }
    public void pushControl(ControlNode n) { reg_c.push(n); }
    public void pushDump(Object n) { reg_d.push(n); }
    public ControlNode popStack() { return reg_s.pop(); }
    public Object popEnv() { return reg_e.pop(); }
    public ControlNode popControl() { return reg_c.pop(); }
    public Object popDump() { return reg_d.pop(); }
    public boolean stackIsEmpty() { return reg_s.isEmpty(); }
    public boolean envIsEmpty() { return reg_e.isEmpty(); }
    public boolean controlIsEmpty() { return reg_c.isEmpty(); }
    public boolean dumpIsEmpty() { return reg_d.isEmpty(); }
    public Stack swapStack(Stack newStack) {
        Stack oldStack = reg_s;
        reg_s = newStack;
        return oldStack;
    }
    public Stack swapEnv(Stack newEnv) {
        Stack oldEnv = reg_e;
        reg_e = newEnv;
        return oldEnv;
    }
    public Stack<ControlNode> swapControl(Stack<ControlNode> newControl) {
        Stack<ControlNode> oldControl = reg_c;
        reg_c = newControl;
        return oldControl;
    }
    public Stack swapDump(Stack newDump) {
        Stack oldDump = reg_d;
        reg_d = newDump;
        return oldDump;
    }
}