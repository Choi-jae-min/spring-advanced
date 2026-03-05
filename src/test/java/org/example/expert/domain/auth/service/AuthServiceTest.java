package org.example.expert.domain.auth.service;

import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.global.security.PasswordEncoder;
import org.example.expert.global.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    public void 회원가입을_성공적으로_완료한다() {
        // given
        SignupRequest request = new SignupRequest("user@email.com", "password123", UserRole.USER);

        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn("encodedPassword");

        User savedUser = new User(request.getEmail(), "encodedPassword", request.getUserRole());
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(jwtUtil.createToken(any(), eq(request.getEmail()), eq(request.getUserRole())))
                .willReturn("Bearer token");

        // when
        SignupResponse response = authService.signup(request);

        // then
        assertNotNull(response);
        assertEquals("Bearer token", response.getBearerToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void 이미_존재하는_이메일로_회원가입시_에러발생() {
        // given
        SignupRequest request = new SignupRequest("duplicate@email.com", "password123", UserRole.USER);

        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            authService.signup(request);
        });

        // then
        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void 로그인을_성공적으로_완료한다() {
        // given
        SigninRequest request = new SigninRequest("user@email.com", "password123");

        User user = new User("user@email.com", "encodedPassword", UserRole.USER);
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getPassword(), user.getPassword())).willReturn(true);
        given(jwtUtil.createToken(any(), eq(user.getEmail()), eq(user.getUserRole())))
                .willReturn("Bearer token");

        // when
        SigninResponse response = authService.signin(request);

        // then
        assertNotNull(response);
        assertEquals("Bearer token", response.getBearerToken());
    }

    @Test
    public void 가입되지_않은_이메일로_로그인시_에러발생() {
        // given
        SigninRequest request = new SigninRequest("unknown@email.com", "password123");

        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            authService.signin(request);
        });

        // then
        assertEquals("가입되지 않은 유저입니다.", exception.getMessage());
    }

    @Test
    public void 비밀번호가_일치하지_않아_로그인시_에러발생() {
        // given
        SigninRequest request = new SigninRequest("user@email.com", "wrongPassword");

        User user = new User("user@email.com", "encodedPassword", UserRole.USER);
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getPassword(), user.getPassword())).willReturn(false);

        // when
        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.signin(request);
        });

        // then
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }
}