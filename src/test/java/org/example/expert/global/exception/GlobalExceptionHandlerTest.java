package org.example.expert.global.exception;

import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.ServerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    //GlobalExceptionHandler는 Spring 없이 순수 Java로 테스트가 가능하다고 함
    private GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void InvalidRequestException_400() {
        // given
        InvalidRequestException ex = new InvalidRequestException("잘못된 요청입니다.");

        // when
        ResponseEntity<Map<String, Object>> response = handler.invalidRequestExceptionException(ex);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("잘못된 요청입니다.", response.getBody().get("message"));
        assertEquals(400, response.getBody().get("code"));
        assertEquals("BAD_REQUEST", response.getBody().get("status"));
    }

    @Test
    void AuthException_401() {
        // given
        AuthException ex = new AuthException("인증이 필요합니다.");

        // when
        ResponseEntity<Map<String, Object>> response = handler.handleAuthException(ex);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("인증이 필요합니다.", response.getBody().get("message"));
        assertEquals(401, response.getBody().get("code"));
        assertEquals("UNAUTHORIZED", response.getBody().get("status"));
    }

    @Test
    void ServerException_500() {
        // given
        ServerException ex = new ServerException("서버 오류입니다.");

        // when
        ResponseEntity<Map<String, Object>> response = handler.handleServerException(ex);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("서버 오류입니다.", response.getBody().get("message"));
        assertEquals(500, response.getBody().get("code"));
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().get("status"));
    }

    @Test
    void MethodArgumentNotValidException_400() throws Exception {
        // given
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new ObjectError("target", "값이 유효하지 않습니다."));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        // when
        ResponseEntity<Map<String, Object>> response = handler.dtoValidation(ex);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("값이 유효하지 않습니다.", response.getBody().get("message"));
    }
}