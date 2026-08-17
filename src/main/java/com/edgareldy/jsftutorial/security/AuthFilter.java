package com.edgareldy.jsftutorial.security;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Reads {@link SessionUserHolder} on every request: redirects unauthenticated
 * users hitting a non-public page to {@code /auth/login.xhtml}, and users
 * lacking the {@code ADMIN} role hitting {@code /admin/*} to an access-denied
 * page.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Inject
    private SessionUserHolder sessionUserHolder;

    @Override
    public void init(FilterConfig filterConfig) {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (!sessionUserHolder.isAuthenticated()) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/login.xhtml");
            return;
        }

        if (path.startsWith("/admin/") && !sessionUserHolder.hasRole("ADMIN")) {
            httpRequest.getRequestDispatcher("/errors/access-denied.xhtml").forward(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        return path.equals("/")
                || path.equals("/index.xhtml")
                || path.startsWith("/auth/")
                || path.startsWith("/errors/")
                || path.startsWith("/javax.faces.resource/")
                || path.startsWith("/resources/");
    }

    @Override
    public void destroy() {
        // no-op
    }
}
