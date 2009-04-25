package org.atfl.runtime.translator;

import java.util.Stack;
import java.util.Vector;
import org.atfl.exception.TranslatorException;
import org.atfl.runtime.ControlNode;
import org.atfl.runtime.ControlNode.OpCode;
import org.atfl.runtime.ControlNode.ControlNodeTag;
import org.atfl.runtime.parser.ParserNode;
import org.atfl.runtime.parser.ParserNode.ParserNodeTag;
import org.atfl.util.SymbolTable;

public class ATFLTranslator {
    private ParserNode AST;
    private ControlNode control;
    private SymbolTable topEnv = new SymbolTable();

    public ATFLTranslator(ParserNode AST) {
        this.AST = AST;
    }

    public void translateTop() throws TranslatorException {
        if (AST.getTag().equals(ParserNodeTag.LIST) == false) {
            throw new TranslatorException("Expected TOP node, got '" +
                    AST.getTag() + "'");
        }

        control = translate(AST, null, null);
    }

    private ControlNode translateList(ParserNode expr, Stack env, ControlNode code) {
        if (expr.equals(null)) {
            code.cons(new ControlNode(ControlNodeTag.NIL));
            code.cons(new ControlNode(ControlNodeTag.INSTR, OpCode.LDC));
            return code;
        }
        else {
            code.cons(new ControlNode(ControlNodeTag.INSTR, OpCode.CONS));
            return translateList(new ParserNode(ParserNodeTag.LIST, expr.cdr()), env,
                    translate((ParserNode)expr.car(), env, code));
        }

    }

    public ControlNode translate(ParserNode expr, Stack env, ControlNode code) {

        if (expr.getTag().equals(ParserNodeTag.ATOM)) {
            code.cons(new ControlNode(ControlNodeTag.SYMBOL, expr.getValue()));
        }
        else if (((ParserNode)expr.car()).getValue().equals("add")) {
            code.cons(new ControlNode(ControlNodeTag.INSTR, OpCode.ADD));
            return translate((ParserNode)expr.cadr(), env,
                    translate((ParserNode)expr.caddr(), env, code));
        }
        else if (((ParserNode)expr.car()).getValue().equals("sub")) {
            code.cons(new ControlNode(ControlNodeTag.INSTR, OpCode.SUB));
            return translate((ParserNode)expr.cadr(), env,
                    translate((ParserNode)expr.caddr(), env, code));
        }
        else if (((ParserNode)expr.car()).getValue().equals("mul")) {
            code.cons(new ControlNode(ControlNodeTag.INSTR, OpCode.MUL));
            return translate((ParserNode)expr.cadr(), env,
                    translate((ParserNode)expr.caddr(), env, code));
        }
        else if (((ParserNode)expr.car()).getValue().equals("div")) {
            code.cons(new ControlNode(ControlNodeTag.INSTR, OpCode.DIV));
            return translate((ParserNode)expr.cadr(), env,
                    translate((ParserNode)expr.caddr(), env, code));
        }
        else if (((ParserNode)expr.car()).getValue().equals("pow")) {
            code.cons(new ControlNode(ControlNodeTag.INSTR, OpCode.POW));
            return translate((ParserNode)expr.cadr(), env,
                    translate((ParserNode)expr.caddr(), env, code));
        }
        else if (((ParserNode)expr.car()).getValue().equals("rem")) {
            code.cons(new ControlNode(ControlNodeTag.INSTR, OpCode.REM));
            return translate((ParserNode)expr.cadr(), env,
                    translate((ParserNode)expr.caddr(), env, code));
        }
        else if (((ParserNode)expr.car()).getValue().equals("car")) {
            code.cons(new ControlNode(ControlNodeTag.INSTR, OpCode.CAR));
            return translate((ParserNode)expr.cadr(), env, code);
        }
        else if (((ParserNode)expr.car()).getValue().equals("cdr")) {
            code.cons(new ControlNode(ControlNodeTag.INSTR, OpCode.CDR));
            return translate((ParserNode)expr.cadr(), env, code);
        }
        else if (((ParserNode)expr.car()).getValue().equals("atom")) {
            code.cons(new ControlNode(ControlNodeTag.INSTR, OpCode.ATOM));
            return translate((ParserNode)expr.cadr(), env, code);
        }
        else if (((ParserNode)expr.car()).getValue().equals("cons")) {
            code.cons(new ControlNode(ControlNodeTag.INSTR, OpCode.CONS));
            return translate((ParserNode)expr.caddr(), env,
                    translate((ParserNode)expr.cadr(), env, code));
        }
        else if (((ParserNode)expr.car()).getValue().equals("if")) {
            ControlNode thenpt = translate((ParserNode) expr.caddr(), env,
                    new ControlNode(ControlNodeTag.LIST,
                    new Vector<ControlNode>().add(new ControlNode(ControlNodeTag.INSTR, OpCode.JOIN))));
            ControlNode elsept = translate((ParserNode) expr.cadddr(), env,
                    new ControlNode(ControlNodeTag.LIST,
                    new Vector<ControlNode>().add(new ControlNode(ControlNodeTag.INSTR, OpCode.JOIN))));
            code.cons(elsept);
            code.cons(thenpt);
            code.cons(new ControlNode(ControlNodeTag.INSTR, OpCode.SEL));
            return translate((ParserNode)expr.cadr(), env, code);
        }
        else if (((ParserNode)expr.car()).getValue().equals("fun")) {
            env.push(expr.cadr()); // is this right???
            ControlNode body = translate((ParserNode)expr.caddr(),
                    env,
                    new ControlNode(ControlNodeTag.LIST,
                        new Vector<ControlNode>().add(new ControlNode(ControlNodeTag.INSTR, OpCode.RTN))));
            code.cons(new ControlNode(ControlNodeTag.LIST, body));
            code.cons(new ControlNode(ControlNodeTag.INSTR, OpCode.LDF));
        }
        else if (((ParserNode)expr.car()).getValue().equals("let")) {

        }
        return null;
    }
}
