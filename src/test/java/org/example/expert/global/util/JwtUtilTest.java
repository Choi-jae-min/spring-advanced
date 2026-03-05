package org.example.expert.global.util;

import org.example.expert.domain.common.exception.ServerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Test
    void substringToken_토큰없으면_예외발생() {
        // given - Bearer prefix 없는 값
        String invalidToken = "InvalidTokenWithoutBearer";

        // when & then
        assertThatThrownBy(() -> jwtUtil.substringToken(invalidToken))
                .isInstanceOf(ServerException.class)
                .hasMessageContaining("Not Found Token");
    }

    @Test
    void substringToken_빈값이면_예외발생() {
        // given - 빈 문자열
        assertThatThrownBy(() -> jwtUtil.substringToken(""))
                .isInstanceOf(ServerException.class)
                .hasMessageContaining("Not Found Token");
    }
}