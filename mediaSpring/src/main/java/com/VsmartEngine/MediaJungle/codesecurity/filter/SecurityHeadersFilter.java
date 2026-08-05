package com.VsmartEngine.MediaJungle.codesecurity.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

// ISO 27001 | Module 3: Code Level Security | Task 1: Secure Coding Standards
// Description: HTTP Filter appending secure coding HTTP response headers (HSTS, X-Content-Type-Options, X-Frame-Options, CSP, X-XSS-Protection).
@Component
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse httpResponse) {
            httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            httpResponse.setHeader("Content-Security-Policy", "default-src * 'unsafe-inline' 'unsafe-eval' data: blob:; script-src * 'unsafe-inline' 'unsafe-eval'; style-src * 'unsafe-inline'; img-src * data: blob:; font-src * data:; media-src * data: blob:; connect-src *;");
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        }
        chain.doFilter(request, response);
    }
}
