package org.atfl.runtime.parser;

import org.atfl.exception.TokenException;
import org.atfl.util.TokenReader;
import org.atfl.exception.ParserException;
import org.atfl.runtime.parser.ParserNode.NodeTag;
import org.atfl.runtime.parser.ParserNode.BuiltIn;

public class ATFLParser {
    private TokenReader tok_reader;
    private ParserNode AST;

    public ATFLParser(TokenReader tok_reader) {
        this.tok_reader = tok_reader;
        AST = new ParserNode(NodeTag.TOP);
    }

    public ParserNode getAST() {
        return AST;
    }

    public void parse() throws ParserException, TokenException {
        while (tok_reader.isEOF() != true) {
            AST.addSubNode(parseTop());
        }
    }

    private ParserNode parseParameterList() throws ParserException, TokenException {
        ParserNode n = new ParserNode(NodeTag.LIST);
        int open_brackets = 0;
        String s = tok_reader.getNextToken();

        if (!s.equals("(")) {
            throw new ParserException("Expected '(' but found '" + s + 
                    "' at line:token" + tok_reader.getLineNumber() + 
                    ":" + tok_reader.getTokenNumber());
        }
        else {
            while ((s = tok_reader.getNextToken()).equals(")") == false) {
                n.addSubNode(new ParserNode(NodeTag.ATOM, s));
            }
        }
        return n;
    }

    private void parseAtomOrList(ParserNode n, int numItems) throws ParserException, TokenException {
        String s = "";
        for (int i = 0; i < numItems; i++) {
            s = tok_reader.getNextToken();
            if (s.equals("(")) {
                tok_reader.putBackToken();
                n.addSubNode(parseTop());
            }
            else if (true) { // add check for proper atom
                n.addSubNode(new ParserNode(NodeTag.ATOM, s));
            }
            else {
                throw new ParserException("Expected list or atom but found '" + s +
                        "' at line:token" + tok_reader.getLineNumber() +
                        ":" + tok_reader.getTokenNumber());
            }
        }
    }

    private void parseQuotes(ParserNode n) throws ParserException, TokenException {
        String s = "";

        s = tok_reader.getNextToken();
        if (!s.equals("\"")) {
            throw new ParserException("Expected \" but found '" + s +
                    "' at line:token " + tok_reader.getLineNumber() +
                    ":" + tok_reader.getTokenNumber());
        }
        s += tok_reader.getNextToken();
        String t = tok_reader.getNextToken();

        if (!t.equals("\"")) {
            throw new ParserException("Expected \" but found '" + s +
                    "' at line:token " + tok_reader.getLineNumber() +
                    ":" + tok_reader.getTokenNumber());
        }
        s += t;
        n.addSubNode(new ParserNode(NodeTag.ATOM, s));
    }

    private ParserNode parseTop() throws ParserException, TokenException {
        ParserNode n = new ParserNode(NodeTag.LIST);
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
                    n.addSubNode(new ParserNode(NodeTag.LIST, parseTop()));
                }
                else if (s.equals(")")) {
                    open_brackets--;
                }
                else if (s.equals("fun")) {
                    ParserNode tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.FUN);

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
                    parseAtomOrList(tmp_n, 1);

                    n.addSubNode(tmp_n);
                }
                else if (s.equals("set")) {
                    ParserNode tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.SET);

                    // variable name
                    s = tok_reader.getNextToken();
                    //add check for proper naming of variable
                    tmp_n.addSubNode(new ParserNode(NodeTag.ATOM, s));

                    parseAtomOrList(tmp_n, 1);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("list")) {
                    ParserNode tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.LIST);
                    while ((s = tok_reader.getNextToken()).equals(")") == false) {
                        if (s.equals("(")) {
                            tok_reader.putBackToken();
                            tmp_n.addSubNode(parseTop());
                        }
                        else tmp_n.addSubNode(new ParserNode(NodeTag.ATOM, s));
                    }
                    open_brackets--;
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("car")) {
                    ParserNode tmp_n = tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.CAR);
                    parseAtomOrList(tmp_n, 1);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("cdr")) {
                    ParserNode tmp_n = tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.CDR);
                    parseAtomOrList(tmp_n, 1);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("atom")) {
                    ParserNode tmp_n = tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.ATOM);
                    parseAtomOrList(tmp_n, 1);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("cons")) {
                    ParserNode tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.CONS);
                    parseAtomOrList(tmp_n, 2);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("eq")) {
                    ParserNode tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.EQ);
                    parseAtomOrList(tmp_n, 2);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("add")) {
                    ParserNode tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.ADD);
                    parseAtomOrList(tmp_n, 2);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("sub")) {
                    ParserNode tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.SUB);
                    parseAtomOrList(tmp_n, 2);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("mul")) {
                    ParserNode tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.MUL);
                    parseAtomOrList(tmp_n, 2);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("div")) {
                    ParserNode tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.DIV);
                    parseAtomOrList(tmp_n, 2);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("pow")) {
                    ParserNode tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.POW);
                    parseAtomOrList(tmp_n, 2);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("rem")) {
                    ParserNode tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.REM);
                    parseAtomOrList(tmp_n, 2);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("leq")) {
                    ParserNode tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.LEQ);
                    parseAtomOrList(tmp_n, 2);
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("print")) {
                    ParserNode tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.PRINT);
                    if (tok_reader.getNextToken().equals("\"")) {
                        tok_reader.putBackToken();
                        parseQuotes(tmp_n);
                    }
                    else {
                        parseAtomOrList(tmp_n, 1);
                    }
                    n.addSubNode(tmp_n);
                }
                else if (s.equals("if")) {
                    ParserNode tmp_n = new ParserNode(NodeTag.BUILTIN, BuiltIn.IF);
                    parseAtomOrList(tmp_n, 3);
                    n.addSubNode(tmp_n);
                }
                else {
                    n.addSubNode(new ParserNode(NodeTag.ATOM, s));
                }
            }
        }

        return n;
    }
}
