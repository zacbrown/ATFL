package org.atfl.runtime.translator;

import java.util.Stack;
import java.util.Vector;
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

    public ATFLTranslator(ParserNode AST) {
        this.AST = AST;
    }

    public void translateTop() throws TranslatorException {
        if (AST.getTag().equals(ParserNodeTag.LIST) == false) {
            throw new TranslatorException("Expected TOP node, got '" +
                    AST.getTag() + "'");
        }

        ControlNode code = new ControlNode(ControlNodeTag.LIST, new Vector<ControlNode>());
        code.addSubNode(new ControlNode(ControlNodeTag.INSTR, OpCode.AP));
        code.addSubNode(new ControlNode(ControlNodeTag.INSTR, OpCode.STOP));
        control = translate((ParserNode)ListUtils.car(AST), new Stack(),
                new ControlNode(ControlNodeTag.LIST, code));
    }

    private ControlNode translateList(ParserNode expr, Stack env, ControlNode code) {
        if (expr.equals(null)) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.NIL));
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.LDC));
            return code;
        }
        else {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.CONS));
            return translateList(new ParserNode(ParserNodeTag.LIST, ListUtils.cdr(expr)), env,
                    translate((ParserNode)ListUtils.car(expr), env, code));
        }

    }

    public ControlNode translate(ParserNode expr, Stack env, ControlNode code) {
        if (expr.getTag().equals(ParserNodeTag.ATOM)) {
            ListUtils.cons(code, new ControlNode(ControlNodeTag.SYMBOL, expr.getValue()));
            return code;
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
            ControlNode thenpt = translate((ParserNode)ListUtils.caddr(expr), env,
                    new ControlNode(ControlNodeTag.LIST,
                    new Vector<ControlNode>().add(new ControlNode(ControlNodeTag.INSTR, OpCode.JOIN))));
            ControlNode elsept = translate((ParserNode)ListUtils.cadddr(expr), env,
                    new ControlNode(ControlNodeTag.LIST,
                    new Vector<ControlNode>().add(new ControlNode(ControlNodeTag.INSTR, OpCode.JOIN))));
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
        ListUtils.cons(code, new ControlNode(ControlNodeTag.INSTR, OpCode.AP));
        return translateList(new ParserNode(ParserNodeTag.LIST, ListUtils.cdr(expr)),
                env, translate((ParserNode)ListUtils.car(expr), env, code));
    }
}
