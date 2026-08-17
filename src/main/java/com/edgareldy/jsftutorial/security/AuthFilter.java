package com.edgareldy.jsftutorial.security;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Plumbing-only servlet filter skeleton mapped to every request; the actual
 * authentication/authorization rules are wired in by feature/auth.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // no-op: plumbing only, feature/auth wires in SessionUserHolder-based
        // authentication/authorization rules
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // TODO(feature/auth): read SessionUserHolder, redirect unauthenticated
        // users hitting a non-public page to /auth/login.xhtml, and users lacking
        // the required role hitting /admin/* to an access-denied page
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // no-op
    }
}
