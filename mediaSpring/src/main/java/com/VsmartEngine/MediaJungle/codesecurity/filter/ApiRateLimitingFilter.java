package com.VsmartEngine.MediaJungle.codesecurity.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// ISO 27001 | Module 3: Code Level Security | Task 6: Secure API Development
// Description: Token Bucket API rate limiting filter restricting request volume per IP address (500 req/min) to prevent brute forcing and denial of service while supporting high-throughput streaming.
@Component
public class ApiRateLimitingFilter implements Filter {

    private static final int MAX_REQUESTS_PER_MINUTE = 500;
    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    private static class RequestCounter {
        int count;
        long startTime;

        RequestCounter(long startTime) {
            this.count = 1;
            this.startTime = startTime;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {
            // ISO 27001 Task 6: CORS Preflight Exemption
            if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                chain.doFilter(request, response);
                return;
            }

            String clientIp = httpRequest.getRemoteAddr();
            long currentTime = System.currentTimeMillis();

            RequestCounter counter = requestCounts.compute(clientIp, (ip, current) -> {
                if (current == null || (currentTime - current.startTime > 60000)) {
                    return new RequestCounter(currentTime);
                } else {
                    current.count++;
                    return current;
                }
            });

            if (counter.count > MAX_REQUESTS_PER_MINUTE) {
                httpResponse.setStatus(429); // 429 Too Many Requests
                httpResponse.getWriter().write("{\"message\": \"API rate limit exceeded. Maximum 500 requests per minute allowed.\"}");
                return;
            }

            httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_MINUTE));
            httpResponse.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, MAX_REQUESTS_PER_MINUTE - counter.count)));
        }
        chain.doFilter(request, response);
    }
}
