package com.gymmick.smartgym.middleware;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.util.WebUtils;

import com.gymmick.smartgym.entity.Token;
import com.gymmick.smartgym.entity.enums.TokenType;
import com.gymmick.smartgym.services.auth.TokenService;
import com.gymmick.smartgym.services.user.UserService;
import com.gymmick.smartgym.utils.JwtToken;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthMiddleware extends OncePerRequestFilter {

    private final UserService userService;
    private final TokenService tokenService;

    private final JwtToken jwtToken;

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        return path.startsWith("/auth/") || path.startsWith("/midtrans/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            Cookie accessToken = WebUtils.getCookie(request, "access-token");

            if (accessToken != null && !accessToken.getValue().isEmpty()) {

                UUID tokenId = jwtToken.getTokenId(accessToken.getValue());
                Token token = tokenService.findById(tokenId);

                if (token != null && !token.getRevoked() && token.getType().equals(TokenType.AUTH_USER)
                        && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails = userService.loadUserByUsername(token.getUser().getEmail());

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());

                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }

}
