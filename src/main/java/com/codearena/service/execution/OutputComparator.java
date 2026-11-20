package com.codearena.service.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OutputComparator {

    public static boolean isMatch(String actual, String expected) {
        if (actual == null && expected == null) {
            return true;
        }
        if (actual == null || expected == null) {
            return false;
        }

        String normActual = normalizeLineEndings(actual).trim();
        String normExpected = normalizeLineEndings(expected).trim();

        // 1. Exact string match after trimming
        if (normActual.equals(normExpected)) {
            return true;
        }

        // 2. Case-insensitive boolean comparison
        if (isBoolean(normExpected)) {
            return normActual.equalsIgnoreCase(normExpected);
        }

        // 3. Numeric comparison (e.g. 23 == 23.0)
        try {
            double dActual = Double.parseDouble(normActual);
            double dExpected = Double.parseDouble(normExpected);
            if (Math.abs(dActual - dExpected) < 1e-6) {
                return true;
            }
        } catch (NumberFormatException ignored) {
        }

        // 4. Bracketed array / list comparison (e.g. ["(())","()()"] vs [(()), ()()])
        if (isBracketed(normActual) && isBracketed(normExpected)) {
            List<String> actualTokens = tokenizeList(normActual);
            List<String> expectedTokens = tokenizeList(normExpected);

            if (actualTokens.equals(expectedTokens)) {
                return true;
            }

            // Permutation / order-insensitive list comparison
            if (actualTokens.size() == expectedTokens.size()) {
                List<String> sortedActual = new ArrayList<>(actualTokens);
                List<String> sortedExpected = new ArrayList<>(expectedTokens);
                Collections.sort(sortedActual);
                Collections.sort(sortedExpected);
                if (sortedActual.equals(sortedExpected)) {
                    return true;
                }
            }
        }

        // 5. Line-by-line whitespace-trimmed comparison
        String[] actualLines = normActual.split("\n");
        String[] expectedLines = normExpected.split("\n");
        if (actualLines.length == expectedLines.length) {
            boolean allLinesMatch = true;
            for (int i = 0; i < actualLines.length; i++) {
                if (!actualLines[i].trim().equals(expectedLines[i].trim())) {
                    allLinesMatch = false;
                    break;
                }
            }
            if (allLinesMatch) {
                return true;
            }
        }

        return false;
    }

    private static String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n").replace("\r", "\n");
    }

    private static boolean isBoolean(String text) {
        return "true".equalsIgnoreCase(text) || "false".equalsIgnoreCase(text);
    }

    private static boolean isBracketed(String text) {
        return text.startsWith("[") && text.endsWith("]");
    }

    private static List<String> tokenizeList(String bracketed) {
        String content = bracketed.substring(1, bracketed.length() - 1).trim();
        if (content.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int bracketDepth = 0;
        boolean inQuotes = false;
        char quoteChar = 0;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (inQuotes) {
                if (c == quoteChar) {
                    inQuotes = false;
                } else if (c == '\\' && i + 1 < content.length()) {
                    current.append(c).append(content.charAt(++i));
                    continue;
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"' || c == '\'') {
                    inQuotes = true;
                    quoteChar = c;
                } else if (c == '[' || c == '{') {
                    bracketDepth++;
                    current.append(c);
                } else if (c == ']' || c == '}') {
                    bracketDepth--;
                    current.append(c);
                } else if (c == ',' && bracketDepth == 0) {
                    tokens.add(cleanToken(current.toString()));
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }

        if (current.length() > 0) {
            tokens.add(cleanToken(current.toString()));
        }

        return tokens;
    }

    private static String cleanToken(String token) {
        String trimmed = token.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        } else if (trimmed.startsWith("'") && trimmed.endsWith("'") && trimmed.length() >= 2) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed.trim();
    }
}
