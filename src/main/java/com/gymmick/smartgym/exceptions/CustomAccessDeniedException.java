package com.gymmick.smartgym.exceptions;

import java.io.IOException;
import java.util.*;

import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.*;
import tools.jackson.databind.ObjectMapper;

@Component
public class CustomAccessDeniedException implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("statusCode", HttpStatus.FORBIDDEN.value());
        body.put("message", "You don't have permission to access this data!");

        final ObjectMapper objectMapper = new ObjectMapper();

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
