package com.sht.smartlock.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenjl on 2015/7/6.
 */
public class TextUtil {
    private static final String IMPLICIT_HTML_OF_START = "<| html - ";
    private static final String IMPLICIT_HTML_OF_END = " - html |>";
    private static final String HTML_OF_START = "# ";
    private static final String HTML_OF_END = " #";

    public static String getImplicitHtmlText(String text) {
        Pattern startPattern = Pattern.compile(HTML_OF_START);
        Matcher startMatcher = startPattern.matcher(text);
        text = startMatcher.replaceAll(IMPLICIT_HTML_OF_START);

        Pattern endPattern = Pattern.compile(HTML_OF_END);
        Matcher endMatcher = endPattern.matcher(text);
        text = endMatcher.replaceAll(IMPLICIT_HTML_OF_END);
        return text;
    }
}
