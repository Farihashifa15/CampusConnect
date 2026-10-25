package com.campusconnect.filter;

import com.campusconnect.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    // Public URI prefixes that do NOT require authentication
    private static final List<String> PUBLIC_PREFIXES = Arrays.asList(
        "/auth/login",
        "/auth/register",
        "/assets/",
        "/css/",
        "/js/",
        "/images/",
        "/index.jsp",
        "/home"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {
            String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

            // Exact home page or public prefix
            boolean isPublic = "/".equals(path) || "".equals(path);
            if (!isPublic) {
                for (String prefix : PUBLIC_PREFIXES) {
                    if (path.startsWith(prefix)) {
                        isPublic = true;
                        break;
                    }
                }
            }

            if (!isPublic) {
                HttpSession session = httpRequest.getSession(false);
                User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

                if (currentUser == null) {
                    // Redirect to login
                    String redirectUrl = httpRequest.getRequestURI();
                    if (httpRequest.getQueryString() != null) {
                        redirectUrl += "?" + httpRequest.getQueryString();
                    }
                    httpRequest.getSession(true).setAttribute("redirectAfterLogin", redirectUrl);
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/login?error=auth_required");
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }
}