package org.atfl.runtime.parser;

import org.atfl.exception.TokenException;
import org.atfl.util.TokenReader;
import org.atfl.exception.ParserException;
import org.atfl.runtime.parser.Node.NodeTag;
import org.atfl.runtime.parser.Node.BuiltIn;

public class ATFLParser {
    private TokenReader tok_reader;
    private Node AST;

    public ATFLParser(TokenReader tok_reader) {
        this.tok_reader = tok_reader;
        AST = new Node(NodeTag.TOP);
    }

    public Node getAST() {
        return AST;
    }

    public void parse() throws ParserException, TokenException {
        while (tok_reader.isEOF() != true) {
            AST.addSubNode(parseTop());
        }
    }

    private Node parseParameterList() throws ParserException, TokenException {
        Node n = new Node(NodeTag.LIST);
        int open_brackets = 0;
        String s = tok_reader.getNextToken();

        if (!s.equals("(")) {
            throw new ParserException("Expected '(' but found '" + s + 
                    "' at line:token" + tok_reader.getLineNumber() + 
                    ":" + tok_reader.getTokenNumber());
        }
        else {
            while ((s = tok_reader.getNextToken()).equals(")") == false) {
                n.addSubNode(new Node(NodeTag.ATOM, s));
            }
        }
        return n;
    }

    private void parse1AtomOrList(Node n) throws ParserException, TokenException {
        String s = "";

        s = tok_reader.getNextToken();
        if (s.equals("(")) {
            tok_reader.putBackToken();
            n.addSubNode(parseTop());
        }
        else if (true) { // add check for proper atom
            n.addSubNode(new Node(NodeTag.ATOM, s));
        }
        else {
            throw new ParserException("Expected list or atom but found '" + s +
                    "' at line:token" + tok_reader.getLineNumber() +
                    ":" + tok_reader.getTokenNumber());
        }
    }
    
    private void parse2AtomOrList(Node n) throws ParserException, TokenException {
        String s = "";

        s = tok_reader.getNextToken();
        if (s.equals("(")) {
            tok_reader.putBackToken();
            n.addSubNode(parseTop());
        }
        else if (true) { // add check for proper atom
            n.addSubNode(new Node(NodeTag.ATOM, s));
        }
        else {
            throw new ParserException("Expected list or atom but found '" + s +
                    "' at line:token" + tok_reader.getLineNumber() +
                    ":" + tok_reader.getTokenNumber());
        }

        s = tok_reader.getNextToken();
        if (s.equals("(")) {
            tok_reader.putBackToken();
            n.addSubNode(parseTop());
        }
        else if (true) { // add check for proper atom
            n.addSubNode(new Node(NodeTag.ATOM, s));
        }
        else {
            throw new ParserException("Expected list or atom but found '" + s +
                    "' at line:token" + tok_reader.getLineNumber() +
                    ":" + tok_reader.getTokenNumber());
        }

    }

    private Node parseTop() throws ParserException, TokenException {
        Node n = new Node(NodeTag.LIST);
        int open_brackets = 0;
        String s = tok_reader.getNextToken();
        if (s == null) {
            throw new ParserException("Unexpected EOF at line:token " +
                    tok_reader.getLineNumber() + ":" + tok_reader.getTokenNumber());
        }
        if (!s.equals("(")) {
            throw new ParserException("Expected '(' but found '" + s +
                    "' at line:token" + tok_reader.getLineNumber() +
                    ":" + tok_reader.getTokenNumber());
        }
        else {
            open_brackets++;
            while (open_brackets > 0) {
                s = tok_reader.getNextToken();
                if (s.equals("(")) {
                    tok_reader.putBackToken();
                    n.addSubNode(new Node(NodeTag.LIST, parseTop()));
                }
                else if (s.equals(")")) {
                    open_brackets--;
                }
                else if (s.equals("fun")) {
                    Node tmp_n = new Node(NodeTag.BUILTIN, BuiltIn.FUN);

                    // arguments
                    s = tok_reader.getNextToken();
                    if (!s.equals("(")) {
                        throw new ParserException("Expected '(' but found '" + s +
                                "' at line:token" + tok_reader.getLineNumber() +
                                ":" + tok_reader.getTokenNumber());
                    }
                    else {
                        tok_reader.putBackToken();
                        tmp_n.addSubNode(parseParameterList());
                    }

                    // function body
                    parse1AtomOrList(tmp_n);

                    n.addSubNode(tmp_n);
                }
                else if (s.equals("set")) {
                    Node tmp_n = new Node(NodeTag.BUILTIN, BuiltIn.SET);

                    // variable name
                    s = tok_reader.getNextToken();
                    //add check for proper naming of variable
                    tmp_n.addSubNode(new Node(NodeTag.ATOM, s));

                    parse1AtomOrList(tmp_n);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("list")) {
                    Node tmp_n = new Node(NodeTag.BUILTIN, BuiltIn.LIST);
                    while ((s = tok_reader.getNextToken()).equals(")") == false) {
                        if (s.equals("(")) {
                            tok_reader.putBackToken();
                            tmp_n.addSubNode(parseTop());
                        }
                        else tmp_n.addSubNode(new Node(NodeTag.ATOM, s));
                    }
                    open_brackets--;
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("car")) {
                    Node tmp_n = tmp_n = new Node(NodeTag.BUILTIN, BuiltIn.CAR);
                    parse1AtomOrList(tmp_n);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("cdr")) {
                    Node tmp_n = tmp_n = new Node(NodeTag.BUILTIN, BuiltIn.CDR);
                    parse1AtomOrList(tmp_n);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("atom")) {
                    Node tmp_n = tmp_n = new Node(NodeTag.BUILTIN, BuiltIn.ATOM);
                    parse1AtomOrList(tmp_n);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("cons")) {
                    Node tmp_n = new Node(NodeTag.BUILTIN, BuiltIn.CONS);
                    parse2AtomOrList(tmp_n);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("eq")) {
                    Node tmp_n = new Node(NodeTag.BUILTIN, BuiltIn.EQ);
                    parse2AtomOrList(tmp_n);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("add")) {
                    Node tmp_n = new Node(NodeTag.BUILTIN, BuiltIn.ADD);
                    parse2AtomOrList(tmp_n);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("sub")) {
                    Node tmp_n = new Node(NodeTag.BUILTIN, BuiltIn.SUB);
                    parse2AtomOrList(tmp_n);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("mul")) {
                    Node tmp_n = new Node(NodeTag.BUILTIN, BuiltIn.MUL);
                    parse2AtomOrList(tmp_n);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("div")) {
                    Node tmp_n = new Node(NodeTag.BUILTIN, BuiltIn.DIV);
                    parse2AtomOrList(tmp_n);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("pow")) {
                    Node tmp_n = new Node(NodeTag.BUILTIN, BuiltIn.POW);
                    parse2AtomOrList(tmp_n);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("rem")) {
                    Node tmp_n = new Node(NodeTag.BUILTIN, BuiltIn.REM);
                    parse2AtomOrList(tmp_n);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("leq")) {
                    Node tmp_n = new Node(NodeTag.BUILTIN, BuiltIn.LEQ);
                    parse2AtomOrList(tmp_n);
                    n.addSubNode(tmp_n);
                }
                else {
                    n.addSubNode(new Node(NodeTag.ATOM, s));
                }
            }
        }

        return n;
    }
}
