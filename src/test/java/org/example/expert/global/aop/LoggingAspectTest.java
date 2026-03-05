package org.example.expert.global.aop;

import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.global.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoggingAspectTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LoggingAspect loggingAspect;

    private String token;

    @BeforeEach
    void setUp() {
        token = jwtUtil.createToken(1L, "admin@email.com", UserRole.ADMIN);
    }

    @Test
    void 어드민_API_호출시_로그가_찍힌다() throws Exception {


    }

    @Test
    void HTTP_요청_외부에서_호출시_예외발생() {
        // given - HTTP 컨텍스트 제거
        RequestContextHolder.resetRequestAttributes();

        // when & then
        assertThatThrownBy(() -> loggingAspect.logAdminApi(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("HTTP 요청 내부에서만 실행 가능합니다");
    }
}