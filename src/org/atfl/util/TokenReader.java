package org.atfl.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.ArrayList;
import org.atfl.exception.TokenException;

public class TokenReader {

    private int tokenIndex;
    private int lineNum;
    private boolean EOF;
    private ArrayList<String[]> tokens;
    private static Pattern regex_match = 
            Pattern.compile("[()]|[A-Za-z]*[_][A-Za-z0-9]*|[A-Za-z0-9]*");
    private static TokenGenerator tokenizer = new TokenGenerator(regex_match);

    public TokenReader(String filename) throws FileNotFoundException {
        tokens = new ArrayList<String[]>();
        lineNum = 1;
        BufferedReader br =
                new BufferedReader(new FileReader(new File(filename)));

        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                if (line.trim().length() > 0 && 
                    line.trim().substring(0,1).equals(";") == false)
                {
                    buildTokenArray(line);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void buildTokenArray(String stream) {
        /* want to build a token array using some sort of regexps */
        tokens.add(tokenizer.split(stream));
        tokenIndex = 0;
    }

    public String getNextToken() throws TokenException {
        if (lineNum > tokens.size()) { return null; }
        String[] curLine = tokens.get(lineNum - 1);
        if (curLine == null && EOF == true) {
            throw new TokenException("Did not expect current line to be null and also be EOF.");
        }

        int curLineLen = tokens.get(lineNum - 1).length;

        if (tokenIndex < curLineLen) {
            int curTokenIndex = ++tokenIndex;
            if (tokenIndex >= curLineLen) {
                lineNum++;
                tokenIndex = 0;
                if (lineNum > tokens.size()) {
                    EOF = true;
                }
            }
            return curTokenIndex > 0 ? curLine[curTokenIndex - 1] : curLine[curTokenIndex];
        }
        else {
            return null;
        }
    }

    public String[] getTillClosingBracket() throws TokenException {
        ArrayList<String> arraylist = new ArrayList<String>();
        String[] ret_array;
        int num_open_brackets = 0;
        
        String token = "";
        while ((token = getNextToken()) != null && num_open_brackets != -1) {
            if (token.equals("(")) num_open_brackets++;
            if (token.equals(")")) num_open_brackets--;
            arraylist.add(token);
            System.out.println("added: "+token);
        }

        if (num_open_brackets < 0) {
            throw new TokenException("Bracket mismatch, missing open bracket '('");
        }
        else if (num_open_brackets > 0) {
            throw new TokenException("Bracket mismatch, missing closing bracket '(");
        }
 
        ret_array = new String[arraylist.size()];
        arraylist.toArray(ret_array);
        return ret_array;
    }

    public void putBackToken() {
        String[] curLine = tokens.get(lineNum - 1);
        int curLineLen = curLine.length;
        tokenIndex--;
        if (tokenIndex < 0) {
            if ((lineNum - 1) > 0) {
                lineNum--;
                tokenIndex = 0;
            }
            else tokenIndex = 0;
        }
        if (tokenIndex < curLineLen && lineNum == tokens.size()) {
            EOF = false;
        }
    }
    
    public boolean isEOF() { return EOF; }
    public int getLineNumber() { return lineNum; }
    public int getTokenNumber() { return tokenIndex; }

}
