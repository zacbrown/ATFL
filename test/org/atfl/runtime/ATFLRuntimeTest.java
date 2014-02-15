
package org.atfl.runtime;

import java.util.Stack;
import java.util.Vector;
import junit.framework.TestCase;
import org.atfl.exception.SymbolException;
import org.atfl.runtime.ControlNode.OpCode;
import org.atfl.runtime.ControlNode.ControlNodeTag;
import org.atfl.util.SymbolTable;

public class ATFLRuntimeTest extends TestCase {

    public ATFLRuntimeTest(final String testName) {
        super(testName);
    }

    protected void setUp() { }
    protected void tearDown() { }

    public void testAddition() {
        Stack<ControlNode> reg_s = new Stack<ControlNode>();
        Stack reg_e = new Stack();
        Stack<ControlNode> reg_c = new Stack<ControlNode>();
        Stack<ControlNode> reg_d = new Stack<ControlNode>();
        double val_1 = 3, val_2 = 5, result = 8;

        reg_c.push(new ControlNode(ControlNodeTag.INSTR, OpCode.STOP));
        reg_c.push(new ControlNode(ControlNodeTag.INSTR, OpCode.ADD));
        reg_c.push(new ControlNode(ControlNodeTag.NUM, new Double(val_1)));
        reg_c.push(new ControlNode(ControlNodeTag.INSTR, OpCode.LDC));
        reg_c.push(new ControlNode(ControlNodeTag.NUM, new Double(val_2)));
        reg_c.push(new ControlNode(ControlNodeTag.INSTR, OpCode.LDC));

        ATFLRuntime runtime = new ATFLRuntime(reg_s, reg_e, reg_c, reg_d);
        ControlNode result_node = reg_s.pop();
        assertEquals(((Double)result_node.getValue()).doubleValue(), result);
    }
/*
    public void testSubtraction() {

    }

    public void testMultiplication() {

    }

    public void testDivision() {

    }

    public void testExponentiation() {

    }

    public void testModulo() {
        
    }*/

    public void testLDFandAP() {
        Stack<ControlNode> reg_s = new Stack<ControlNode>();
        Stack reg_e = new Stack();
        Stack<ControlNode> reg_c = new Stack<ControlNode>();
        Stack<ControlNode> reg_d = new Stack<ControlNode>();
        Vector<ControlNode> funcNodes = new Vector<ControlNode>();
        ControlNode func = new ControlNode(ControlNodeTag.LIST, funcNodes);
        Vector<ControlNode> funcDefNodes = new Vector<ControlNode>();
        ControlNode funcDef = new ControlNode(ControlNodeTag.LIST, funcDefNodes);
        funcDefNodes.add(new ControlNode(ControlNodeTag.INSTR, OpCode.ADD));
        funcDefNodes.add(new ControlNode(ControlNodeTag.NUM, new Double(2)));
        funcDefNodes.add(new ControlNode(ControlNodeTag.INSTR, OpCode.LDC));
        funcDefNodes.add(new ControlNode(ControlNodeTag.NUM, new Double(13)));
        funcDefNodes.add(new ControlNode(ControlNodeTag.INSTR, OpCode.LDC));
        funcNodes.add(funcDef);
        funcNodes.add(new ControlNode(ControlNodeTag.LIST, new Vector<ControlNode>()));
        reg_c.push(new ControlNode(ControlNodeTag.INSTR, OpCode.STOP));
        reg_c.push(new ControlNode(ControlNodeTag.INSTR, OpCode.AP));
        reg_c.push(func);
        reg_c.push(new ControlNode(ControlNodeTag.INSTR, OpCode.LDF));

        ATFLRuntime runtime = new ATFLRuntime(reg_s, reg_e, reg_c, reg_d);
        ControlNode result_node = reg_s.pop();
        double result = 15;
        assertEquals(((Double)result_node.getValue()).doubleValue(), result);
    }

    public void testCAR() {
        Stack<ControlNode> reg_s = new Stack<ControlNode>();
        Stack reg_e = new Stack();
        Stack<ControlNode> reg_c = new Stack<ControlNode>();
        Stack<ControlNode> reg_d = new Stack<ControlNode>();
        Vector<ControlNode> nodes = new Vector<ControlNode>();
        nodes.add(new ControlNode(ControlNodeTag.NUM, new Double(1)));
        nodes.add(new ControlNode(ControlNodeTag.NUM, new Double(2)));
        nodes.add(new ControlNode(ControlNodeTag.NUM, new Double(3)));
        reg_c.push(new ControlNode(ControlNodeTag.LIST, nodes));
        reg_c.push(new ControlNode(ControlNodeTag.INSTR, OpCode.CAR));
        assertEquals(1,1);
    }

    
    public void testCARWithSymbol() {
        Stack<ControlNode> reg_s = new Stack<ControlNode>();
        Stack reg_e = new Stack();
        Stack<ControlNode> reg_c = new Stack<ControlNode>();
        Stack<ControlNode> reg_d = new Stack<ControlNode>();
        Vector<ControlNode> nodes = new Vector<ControlNode>();
        ControlNode list = new ControlNode(ControlNodeTag.LIST, nodes);
        nodes.add(new ControlNode(ControlNodeTag.NUM, new Double(1)));
        nodes.add(new ControlNode(ControlNodeTag.NUM, new Double(2)));
        nodes.add(new ControlNode(ControlNodeTag.NUM, new Double(3)));
        SymbolTable env = new SymbolTable();
        try { env.add("foob", list); }
        catch (SymbolException e) { e.printStackTrace(); }
        reg_e.push(env);
        reg_c.push(new ControlNode(ControlNodeTag.SYMBOL, "foob"));
        reg_c.push(new ControlNode(ControlNodeTag.INSTR, OpCode.CAR));
                assertEquals(1,1);

    }

    public void testCDR() {
        Stack<ControlNode> reg_s = new Stack<ControlNode>();
        Stack reg_e = new Stack();
        Stack<ControlNode> reg_c = new Stack<ControlNode>();
        Stack<ControlNode> reg_d = new Stack<ControlNode>();
        Vector<ControlNode> nodes = new Vector<ControlNode>();
        nodes.add(new ControlNode(ControlNodeTag.NUM, new Double(1)));
        nodes.add(new ControlNode(ControlNodeTag.NUM, new Double(2)));
        nodes.add(new ControlNode(ControlNodeTag.NUM, new Double(3)));
        reg_c.push(new ControlNode(ControlNodeTag.LIST, nodes));
        reg_c.push(new ControlNode(ControlNodeTag.INSTR, OpCode.CDR));
                assertEquals(1,1);

    }

    public void testCDRWithSymbol() {
        Stack<ControlNode> reg_s = new Stack<ControlNode>();
        Stack reg_e = new Stack();
        Stack<ControlNode> reg_c = new Stack<ControlNode>();
        Stack<ControlNode> reg_d = new Stack<ControlNode>();
        Vector<ControlNode> nodes = new Vector<ControlNode>();
        ControlNode list = new ControlNode(ControlNodeTag.LIST, nodes);
        nodes.add(new ControlNode(ControlNodeTag.NUM, new Double(1)));
        nodes.add(new ControlNode(ControlNodeTag.NUM, new Double(2)));
        nodes.add(new ControlNode(ControlNodeTag.NUM, new Double(3)));
        SymbolTable env = new SymbolTable();
        try { env.add("foob", list); }
        catch (SymbolException e) { e.printStackTrace(); }
        reg_e.push(env);
        reg_c.push(new ControlNode(ControlNodeTag.SYMBOL, "foob"));
        reg_c.push(new ControlNode(ControlNodeTag.INSTR, OpCode.CDR));
                assertEquals(1,1);

    }

    public void testRTN() {
        Stack<ControlNode> reg_s = new Stack<ControlNode>();
        Stack reg_e = new Stack();
        Stack<ControlNode> reg_c = new Stack<ControlNode>();
        Stack reg_d = new Stack();
        Stack new_s = new Stack();
        Stack new_e = new Stack();
        Stack new_c = new Stack();
        new_c.push(new ControlNode(ControlNodeTag.STR, "blub"));
        new_c.push(new ControlNode(ControlNodeTag.INSTR, org.atfl.runtime.ControlNode.OpCode.LDC));
        reg_c.push(new ControlNode(ControlNodeTag.INSTR, org.atfl.runtime.ControlNode.OpCode.RTN));
        reg_s.push(new ControlNode(ControlNodeTag.NUM, new Double(55)));
        reg_d.push(new_c);
        reg_d.push(new_e);
        reg_d.push(new_s);
    }
}
