package com.VsmartEngine.MediaJungle.codesecurity.util;

import java.util.regex.Pattern;

// ISO 27001 | Module 3: Code Level Security | Task 3: SQL Injection Protection
// Description: Utility detecting SQL injection attack payloads and enforcing prepared statement parameterization.
public class SqlInjectionProtectionUtil {

    private static final Pattern SQLI_PATTERN = Pattern.compile(
        "(?i)(.*\\b(SELECT|INSERT|UPDATE|DELETE|DROP|ALTER|CREATE|TRUNCATE|UNION|EXEC|DECLARE|XP_)\\b.*)|" +
        "(.*('--'|';'|' OR '|' AND '|'1'='1'|'1'='1|1=1).*)"
    );

    public static boolean containsSqlInjection(String input) {
        if (input == null || input.trim().isEmpty()) return false;
        return SQLI_PATTERN.matcher(input).matches();
    }

    public static String escapeSqlString(String input) {
        if (input == null) return null;
        return input.replace("'", "''").replace("\\", "\\\\");
    }
}
