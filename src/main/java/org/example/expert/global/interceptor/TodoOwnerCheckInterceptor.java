package org.example.expert.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.service.TodoAuthorization;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TodoOwnerCheckInterceptor implements HandlerInterceptor {

    private final TodoAuthorization todoAuthorization;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        Long userId = (Long) request.getAttribute("userId");

        Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        todoAuthorization.validateOwner(Long.valueOf(pathVariables.get("todoId")), userId);
        return true;
    }
}
