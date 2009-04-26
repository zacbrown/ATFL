package org.atfl.runtime.translator;

import java.util.Collections;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Pattern;
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
    private SymbolTable topEnv = new SymbolTable();
    private static Pattern regexNum = Pattern.compile("[-]?[0-9]+(\\.[0-9]+)?");
    private static Pattern regexBool = Pattern.compile("True|False");

    public ATFLTranslator(ParserNode AST) {
        this.AST = AST;
    }

    public ControlNode getControl() { return control; }
    public SymbolTable getEnv() { return topEnv; }

    public void translateTop() throws TranslatorException {
        if (AST.getTag().equals(ParserNodeTag.LIST) == false) {
            throw new TranslatorException("Expected TOP node, got '" +
                    AST.getTag() + "'");
        }

        ControlNode code = new ControlNode(ControlNodeTag.LIST, new Vector<ControlNode>());
        //code.addSubNode(new ControlNode(ControlNodeTag.INSTR, OpCode.AP));
        code.addSubNode(new ControlNode(ControlNodeTag.INSTR, OpCode.STOP));
        Vector<ParserNode> nodes = AST.getSubNodes();
        int size = nodes.size();
        for (int i = size - 1; i >= 0; i--) {
            control = translate((ParserNode)nodes.get(i), new Stack(), code);
        }
        Collections.reverse(control.getSubNodes());
    }

    public ControlNode translate(ParserNode expr, Stack env, ControlNode code) throws TranslatorException {
        if (expr.getTag().equals(ParserNodeTag.ATOM)) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.SYMBOL, expr.getValue()));
            return code;
        }
        else if (((ParserNode)ListUtils.car(expr)).getValue().equals("quote")) {
            ParserNode node = (ParserNode)ListUtils.cadr(expr);
            String val = (String)node.getValue();
            if (regexNum.matcher(val).matches()) {
                ListUtils.cons(code, new ControlNode(ControlNodeTag.NUM, new Double(val)));
            } else if (val.equals("True")) {
                ListUtils.cons(code, new ControlNode(ControlNodeTag.BOOL, Boolean.TRUE));
            } else if (val.equals("False")) {
                ListUtils.cons(code, new ControlNode(ControlNodeTag.BOOL, Boolean.FALSE));
            } else if (val.charAt(0) == '\"' && val.charAt(val.length()-1) == '\"') {
                ListUtils.cons(code, new ControlNode(ControlNodeTag.STR,
                        val.substring(1, val.length()-1)));
            } else {
                throw new TranslatorException("Invalid atom given: '" + val + "'");
            }

            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.LDC));
            return code;
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
            return translate((ParserNode)ListUtils.caar(expr), env, code);
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
            env.push(ListUtils.cadr(expr)); // is this right???
            ControlNode body = translate((ParserNode)ListUtils.caddr(expr),
                    env,
                    new ControlNode(ControlNodeTag.LIST,
                        new Vector<ControlNode>().add(new ControlNode(ControlNodeTag.INSTR, OpCode.RTN))));
            ListUtils.cons(code, new ControlNode(ControlNodeTag.LIST, body));
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.LDF));
            return code;
        }
        return code;
    }
}
