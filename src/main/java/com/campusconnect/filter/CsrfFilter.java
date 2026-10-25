package com.campusconnect.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@WebFilter("/*")
public class CsrfFilter implements Filter {

    public static final String CSRF_TOKEN_ATTR = "csrfToken";
    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {
            HttpSession session = httpRequest.getSession(true);

            // Ensure a CSRF token exists for the session
            String sessionToken = (String) session.getAttribute(CSRF_TOKEN_ATTR);
            if (sessionToken == null) {
                byte[] randomBytes = new byte[32];
                secureRandom.nextBytes(randomBytes);
                sessionToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
                session.setAttribute(CSRF_TOKEN_ATTR, sessionToken);
            }

            String method = httpRequest.getMethod();
            if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
                String requestToken = httpRequest.getParameter(CSRF_TOKEN_ATTR);
                if (requestToken == null) {
                    requestToken = httpRequest.getHeader("X-CSRF-Token");
                }

                // Exclude multi-part form boundary check or permit if token is in query param
                if (requestToken == null || !requestToken.equals(sessionToken)) {
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }
}