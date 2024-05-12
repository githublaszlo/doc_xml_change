package org.example;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyFinder {
    private final String regex;
    private final String regex2;

    private final String beginRegex;

    private final String endRegex;

    public KeyFinder(String beginRegex, String endRegex) {
        this.beginRegex = beginRegex;
        this.endRegex = endRegex;
        regex = "(?<=" + beginRegex + ").*?(?=" + endRegex + ")";
        regex2 = "(?<=" + beginRegex + ").*+";
    }

    public ArrayList<String> match(String text) {
        ArrayList<String> result = new ArrayList<>();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String match = matcher.group();
            match = matchShortest(match);
            result.add(match);
        }
        return result;
    }

    public <T> ArrayList<String> match(String text, Map<String, T> change) {
        ArrayList<String> result = match(text);
        result.removeIf(e -> !change.containsKey(e));
        return result;
    }

    private String matchShortest(String text) {
        Pattern pattern = Pattern.compile(regex2);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String temptex = matcher.group();
            return matchShortest(temptex);
        } else {
            return text;
        }
    }

    public ArrayList<Integer> startPosFind(String text, ArrayList<String> keysInDocx) {
        ArrayList<Integer> result = new ArrayList<>();
        int fromPos = 0;
        for (int i = 0; i < keysInDocx.size(); i++) {
            int findPos = text.indexOf(beginRegexNoBackslash() + keysInDocx.get(i) + endRegexNoBackslash(), fromPos);
            result.add(findPos);
            fromPos = findPos + 1;
        }
        return result;
    }

    // The position of the character after endregex
    public ArrayList<Integer> endPosFind(String text, ArrayList<String> keysInDocx) {
        ArrayList<Integer> result = new ArrayList<>();
        int fromPos = 0;
        for (int i = 0; i < keysInDocx.size(); i++) {
            int findPos = text.indexOf(beginRegexNoBackslash() + keysInDocx.get(i) + endRegexNoBackslash(), fromPos);
            result.add(findPos + beginRegexLength() + keysInDocx.get(i).length() + endRegexLength());
            fromPos = findPos + 1;
        }
        return result;
    }

    private int beginRegexLength() {
        int backslashcount = charCount(beginRegex, '\\');
        return beginRegex.length() - backslashcount;
    }

    private int endRegexLength() {
        int backslashcount = charCount(endRegex, '\\');
        return endRegex.length() - backslashcount;
    }

    private String beginRegexNoBackslash() {
        return beginRegex.replaceAll("\\\\", "");
    }

    private String endRegexNoBackslash() {
        return endRegex.replaceAll("\\\\", "");
    }

    private int charCount(String text, char ch) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == ch) {
                count++;
            }
        }
        return count;
    }
}
