package com.VsmartEngine.MediaJungle.codesecurity.util;

// ISO 27001 | Module 3: Code Level Security | Task 4: XSS Protection
// Description: Utility encoding HTML special characters and stripping JavaScript script tags to prevent XSS attacks.
public class XssSanitizerUtil {

    public static String encodeHtml(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '&' -> sb.append("&amp;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&#x27;");
                case '/' -> sb.append("&#x2F;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String stripScriptTags(String input) {
        if (input == null) return null;
        return input.replaceAll("(?i)<script.*?>.*?</script>", "")
                    .replaceAll("(?i)javascript:", "")
                    .replaceAll("(?i)onload=.*?", "");
    }
}
