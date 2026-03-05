package org.example.expert.global.resolver;


import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthUserArgumentResolverTest {
    @InjectMocks
    private AuthUserArgumentResolver resolver;

    @Mock
    private MethodParameter parameter;

    @Test
    void supportsParameter_Auth어노테이션만있고_AuthUser타입만쓰면_예외() {
        // Auth 을 null 로 세팅
        given(parameter.getParameterAnnotation(Auth.class)).willReturn(null);
        // getParameterType 을 AuthUser 로 세팅
        given(parameter.getParameterType()).willReturn((Class) AuthUser.class);

        // when & then
        assertThatThrownBy(() -> resolver.supportsParameter(parameter))
                // 던져진 예외가 AuthException 타입인지 검증
                .isInstanceOf(AuthException.class)
                // 예외 메시지에 해당 문자열이 포함되어 있는지 검증
                .hasMessageContaining("@Auth와 AuthUser 타입은 함께 사용되어야 합니다.");
    }
}