package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class AdminCheckInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AdminCheckInterceptor.class.getName());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // filter 에서 설정한 role, id, url 가져오기
        UserRole userRole = UserRole.valueOf((String) request.getAttribute("userRole"));
        Long userId = (Long) request.getAttribute("userId");
        String url = request.getRequestURI();

        // 권한이 일치한지 검증
        if (!UserRole.ADMIN.equals(userRole)) {
            // 아니라면 에러 리턴
            logger.error("권한 부족: userId={}, role={}, URI={}", userId, userRole, url);
            response.setContentType("application/json;charset=UTF-8");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자만 접근 할 수 있습니다.");
            return false;
        }

        //인증 성공 시, 요청 시각과 URL을 로깅
        String requestDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("요청 시각 = {}, 요청 URL = {}", requestDate, url);
        return true;
    }
}
