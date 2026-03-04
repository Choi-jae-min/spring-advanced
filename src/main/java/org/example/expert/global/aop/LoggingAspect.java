package org.example.expert.global.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {
    private final ObjectMapper objectMapper;

    @Pointcut("execution(* org.example.expert.domain.user.service.UserAdminService.*(..)) || " +
            "execution(* org.example.expert.domain.comment.controller.CommentAdminController.*(..))")
    private void adminApi() {}

    @Around("adminApi()")
    public Object logAdminApi(ProceedingJoinPoint joinPoint) throws Throwable {
        UUID requestId = UUID.randomUUID();
        String requestTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        HttpServletRequest request = getRequest();

        //url 정보
        String url = request.getRequestURI();

        // attribute 에 저장된 userId가져오기
        Long userId = (Long) request.getAttribute("userId");

        // body args list
        Object[] args = joinPoint.getArgs();

        log.info(">>> 요청 ID= {} | 요청 데이터  userId= {} 시간= {} 주소= {} Body= {}",
                requestId ,userId, requestTime, url, objectMapper.writeValueAsString(args));

        Object result;
        //서비스 실행
        result = joinPoint.proceed();

        // 결과 response 반환
        String responseBody = (result != null) ? result.toString() : "No Response";
        log.info("<<< 요청 ID= {} | API Response:[Body: {}]",requestId, responseBody);

        return result;
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(attributes == null){
            log.error("HTTP 요청 컨텍스트를 찾을 수 없습니다. (RequestAttributes is null)");
            throw new IllegalStateException("해당 AOP는 HTTP 요청 내부에서만 실행 가능합니다.");
        }

        return attributes.getRequest();
    }
}
