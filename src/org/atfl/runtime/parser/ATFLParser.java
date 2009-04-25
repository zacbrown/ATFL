package org.atfl.runtime.parser;

import org.atfl.exception.TokenException;
import org.atfl.util.TokenReader;
import org.atfl.exception.ParserException;
import org.atfl.runtime.parser.ParserNode.ParserNodeTag;

public class ATFLParser {
    private TokenReader tok_reader;
    private ParserNode AST;

    public ATFLParser(TokenReader tok_reader) {
        this.tok_reader = tok_reader;
        AST = new ParserNode(ParserNodeTag.LIST);
    }

    public ParserNode getAST() {
        return AST;
    }

    public void parse() throws ParserException, TokenException {
        while (tok_reader.isEOF() != true) {
            AST.addSubNode(parseTop());
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
        n.addSubNode(new ParserNode(ParserNodeTag.ATOM, s));
    }

    private ParserNode parseTop() throws ParserException, TokenException {
        ParserNode n = new ParserNode(ParserNodeTag.LIST);
        int open_brackets = 0;

        String s = tok_reader.getNextToken();
        if (s == null) {
            throw new ParserException("Unexpected EOF at line:token " +
                    tok_reader.getLineNumber() + ":" + tok_reader.getTokenNumber());
        }

        if (!s.equals("(")) {
            throw new ParserException("Expected '(' but found '" + s +
                    "' at line:token " + tok_reader.getLineNumber() + ":" +
                    tok_reader.getTokenNumber());
        }
        else {
            open_brackets++;
            while (open_brackets > 0) {
                s = tok_reader.getNextToken();

                if (s.equals("(")) {
                    tok_reader.putBackToken();
                    n.addSubNode(new ParserNode(ParserNodeTag.LIST, parseTop()));
                }
                else if (s.equals(")")) {
                    open_brackets--;
                }
                else if (s.equals("\"")) {
                    tok_reader.putBackToken();
                    parseQuotes(n);
                }
                else {
                    n.addSubNode(new ParserNode(ParserNodeTag.ATOM, s));
                }
            }
        }

        return n;
    }
}
