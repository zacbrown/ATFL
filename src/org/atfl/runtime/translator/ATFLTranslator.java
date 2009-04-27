package org.atfl.runtime.translator;

import java.util.Collections;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Pattern;
import org.atfl.exception.SymbolException;
import org.atfl.exception.TranslatorException;
import org.atfl.runtime.ControlNode;
import org.atfl.runtime.ControlNode.OpCode;
import org.atfl.runtime.ControlNode.ControlNodeTag;
import org.atfl.runtime.parser.ParserNode;
import org.atfl.runtime.parser.ParserNode.ParserNodeTag;
import org.atfl.util.ListUtils;
import org.atfl.util.SymbolTable;

public class ATFLTranslator {
    private ParserNode AST;
    private ControlNode control;
    private Stack env = new Stack();
    private static Pattern regexNum = Pattern.compile("[-]?[0-9]+(\\.[0-9]+)?");
    private static Pattern regexBool = Pattern.compile("True|False");

    public ATFLTranslator(ParserNode AST) {
        this.AST = AST;
    }

    public ControlNode getControl() { return control; }
    public Stack getEnv() { return env; }

    public void translateTop() throws TranslatorException, SymbolException {
        if (AST.getTag().equals(ParserNodeTag.LIST) == false) {
            throw new TranslatorException("Expected TOP node, got '" +
                    AST.getTag() + "'");
        }

        ControlNode code = new ControlNode(ControlNodeTag.LIST, new Vector<ControlNode>());
        //code.addSubNode(new ControlNode(ControlNodeTag.INSTR, OpCode.AP));
        code.addSubNode(new ControlNode(ControlNodeTag.INSTR, OpCode.STOP));
        Vector<ParserNode> nodes = AST.getSubNodes();
        env.push(new SymbolTable());
        int size = nodes.size();
        for (int i = size - 1; i >= 0; i--) {
            control = translate((ParserNode)nodes.get(i), env, code);
        }
        Collections.reverse(control.getSubNodes());
    }

    private ControlNode translateAtom(ParserNode n, Stack env, boolean ignoreEnv) throws TranslatorException {
        String val = (String) n.getValue();
        ControlNode symbolVal = null;
        if (ignoreEnv == false) {
            symbolVal = ((SymbolTable)env.peek()).get(val);
        }
        if (symbolVal != null) {
            return new ControlNode(ControlNodeTag.SYMBOL, val);
        } else if (regexNum.matcher(val).matches()) {
            return new ControlNode(ControlNodeTag.NUM, new Double(val));
        } else if (val.equals("True")) {
            return new ControlNode(ControlNodeTag.BOOL, Boolean.TRUE);
        } else if (val.equals("False")) {
            return new ControlNode(ControlNodeTag.BOOL, Boolean.FALSE);
        } else if (val.charAt(0) == '\"' && val.charAt(val.length() - 1) == '\"') {
            return new ControlNode(ControlNodeTag.STR, val.substring(1, val.length() - 1));
        } else {
            throw new TranslatorException("Invalid atom given: '" + val + "'");
        }
    }

    private ControlNode translateList(ParserNode n, Stack env) throws TranslatorException {
        Vector<ParserNode> nodes = n.getSubNodes();
        ControlNode retControl = new ControlNode(ControlNodeTag.LIST, new Vector<ControlNode>());
        for (ParserNode node : nodes) {
            retControl.addSubNode(translateAtom(node, env, false));
        }

        return retControl;
    }

    public ControlNode translate(ParserNode expr, Stack env, ControlNode code) throws TranslatorException, SymbolException {
        if (expr.getTag().equals(ParserNodeTag.ATOM)) {
            ControlNode n = translateAtom(expr, env, false);
            ListUtils.cons(code, n);
            if (n.getType().equals(ControlNodeTag.SYMBOL)) {
                ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.LD));
            }
            else {
                ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.LDC));
            }
            return code;
        }
        else if (((ParserNode)ListUtils.car(expr)).getTag().equals(ParserNodeTag.LIST)) {
            if (((ParserNode)ListUtils.caar(expr)).getValue().equals("fun")) {
                Vector<ParserNode> argVals = ((Vector)ListUtils.cdr(expr));
                for (ParserNode arg : argVals) {
                    if (arg.getTag().equals(ParserNodeTag.ATOM)) {
                        env.push(translateAtom(arg, env, true));
                    }
                    else {
                        env.push(translateList(arg, env));
                    }
                }
                ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.AP));
            }
            return translate((ParserNode)ListUtils.car(expr), env, code);
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("print")) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.PRINT));
            return translate((ParserNode)ListUtils.cadr(expr), env, code);
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("println")) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.PRINTLN));
            return translate((ParserNode)ListUtils.cadr(expr), env, code);
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("add")) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.ADD));
            return translate((ParserNode)ListUtils.cadr(expr), env,
                    translate((ParserNode)ListUtils.caddr(expr), env, code));
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("sub")) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.SUB));
            return translate((ParserNode)ListUtils.cadr(expr), env,
                    translate((ParserNode)ListUtils.caddr(expr), env, code));
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("mul")) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.MUL));
            return translate((ParserNode)ListUtils.cadr(expr), env,
                    translate((ParserNode)ListUtils.caddr(expr), env, code));
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("div")) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.DIV));
            return translate((ParserNode)ListUtils.cadr(expr), env,
                    translate((ParserNode)ListUtils.caddr(expr), env, code));
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("pow")) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.POW));
            return translate((ParserNode)ListUtils.cadr(expr), env,
                    translate((ParserNode)ListUtils.caddr(expr), env, code));
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("rem")) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.REM));
            return translate((ParserNode)ListUtils.cadr(expr), env,
                    translate((ParserNode)ListUtils.caddr(expr), env, code));
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("eq")) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.EQ));
            return translate((ParserNode)ListUtils.cadr(expr), env,
                    translate((ParserNode)ListUtils.caddr(expr), env, code));
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("leq")) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.LEQ));
            return translate((ParserNode)ListUtils.cadr(expr), env,
                    translate((ParserNode)ListUtils.caddr(expr), env, code));
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("car")) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.CAR));
            return translate((ParserNode)ListUtils.cadr(expr), env, code);
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("cdr")) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.CDR));
            return translate((ParserNode)ListUtils.cadr(expr), env, code);
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("atom")) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.ATOM));
            return translate((ParserNode)ListUtils.cadr(expr), env, code);
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("cons")) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.CONS));
            return translate((ParserNode)ListUtils.caddr(expr), env,
                    translate((ParserNode)ListUtils.cadr(expr), env, code));
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("if")) {
            ControlNode thenptCode = new ControlNode(ControlNodeTag.LIST, new Vector<ControlNode>());
            thenptCode.addSubNode(new ControlNode(ControlNodeTag.INSTR, OpCode.JOIN));
            ControlNode thenpt = translate((ParserNode)ListUtils.caddr(expr), env, thenptCode);

            ControlNode elseptCode = new ControlNode(ControlNodeTag.LIST, new Vector<ControlNode>());
            elseptCode.addSubNode(new ControlNode(ControlNodeTag.INSTR, OpCode.JOIN));
            ControlNode elsept = translate((ParserNode)ListUtils.cadddr(expr), env, elseptCode);

            Collections.reverse(thenptCode.getSubNodes());
            Collections.reverse(elseptCode.getSubNodes());
            ListUtils.cons(code, elsept);
            ListUtils.cons(code, thenpt);
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.SEL));
            return translate((ParserNode)ListUtils.cadr(expr), env, code);
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("fun")) {
            SymbolTable newEnv = new SymbolTable();

            Vector<ParserNode> args = ((ParserNode)ListUtils.cadr(expr)).getSubNodes();
            int argSize = args.size();

            Vector<ControlNode> argVals = new Vector<ControlNode>();
            for (int i = 0; i < argSize; i++) {
                argVals.add(0, (ControlNode) env.pop());
            }

            for (int i = 0; i < argSize; i++) {
                ParserNode n = args.get(i);
                newEnv.add((String) n.getValue(), argVals.get(i));
            }
            env.push(newEnv);

            ControlNode funControl = new ControlNode(ControlNodeTag.LIST, new Vector<ControlNode>());
            funControl.addSubNode(new ControlNode(ControlNodeTag.INSTR, OpCode.RTN));
            ControlNode body = translate((ParserNode)ListUtils.caddr(expr), env, funControl);
            Collections.reverse(body.getSubNodes());
            ListUtils.cons(code, body);
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.LDF));
            return code;
        }
        return code;
    }
}
