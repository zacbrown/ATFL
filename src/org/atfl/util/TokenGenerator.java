package org.atfl.util;

import java.util.regex.*;
import java.util.LinkedList;

public class TokenGenerator {
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("\\s+");

    private Pattern pattern;
    private boolean keep_delimiters;

    public TokenGenerator(Pattern pattern, boolean keep_delimiters) {
        this.pattern = pattern;
        this.keep_delimiters = keep_delimiters;
    }
    public TokenGenerator(String pattern, boolean keep_delimiters) {
        this(Pattern.compile(pattern==null?"":pattern), keep_delimiters);
    }
    public TokenGenerator(Pattern pattern) { this(pattern, true); }
    public TokenGenerator(String pattern) { this(pattern, true); }
    public TokenGenerator(boolean keep_delimiters) { this(DEFAULT_PATTERN, keep_delimiters); }
    public TokenGenerator() { this(DEFAULT_PATTERN); }

    public String[] split(String text) {
        if (text == null) {
            text = "";
        }

        int last_match = 0;
        LinkedList<String> splitted = new LinkedList<String>();

        Matcher m = this.pattern.matcher(text);

        while (m.find()) {

            splitted.add(text.substring(last_match,m.start()));

            if (this.keep_delimiters) {
                splitted.add(m.group());
            }

            last_match = m.end();
        }

        splitted.add(text.substring(last_match));

        /* clean up empty strings, spaces and tabs */
        while(splitted.remove(" "));
        while(splitted.remove(""));
        while(splitted.remove("\t"));
        while(splitted.remove("\n"));
        while(splitted.remove("\r"));

        return splitted.toArray(new String[splitted.size()]);
    }
}