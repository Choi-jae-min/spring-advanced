package org.example.expert.domain.user.service;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.global.security.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    public void 유저를_성공적으로_조회한다() {
        //given
        Long userId = 1L;
        AuthUser authUser = new AuthUser(1L, "user@email", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        //when
        UserResponse userResponse = userService.getUser(userId);

        //then
        assertEquals(user.getEmail(), userResponse.getEmail());
        assertEquals(user.getId() , userResponse.getId());
    }

    @Test
    public void 유저_비밀번호를_성공적으로_수정한다(){
        // given
        Long userId = 1L;
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        AuthUser authUser = new AuthUser(1L, "user@email", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any()))
                .willReturn(false)
                .willReturn(true);

        // 생성자에는 any()불가
        UserChangePasswordRequest request = new UserChangePasswordRequest(oldPassword, newPassword);

        // when
        userService.changePassword(userId, request);

        // then
        verify(passwordEncoder, times(2)).matches(any(), any());
    }

    @Test
    public void 유저_비밀번호가_기존에_비밀번호와_동일하여_에러발생(){
        // given
        Long userId = 1L;
        String oldPassword = "oldPassword";
        String newPassword = "oldPassword";

        AuthUser authUser = new AuthUser(1L, "user@email", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any()))
                .willReturn(true)
                .willReturn(true);

        UserChangePasswordRequest request = new UserChangePasswordRequest(oldPassword, newPassword);

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            userService.changePassword(userId, request);
        });

        // then
        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.",exception.getMessage());
    }

    @Test
    public void 입력한비밀번호가_기존비밀번호와_일치하지_않는_에러(){
        // given
        Long userId = 1L;
        String wrongPassword = "wrongPassword";
        String newPassword = "newPassword";

        AuthUser authUser = new AuthUser(1L, "user@email", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any()))
                .willReturn(false)
                .willReturn(false);

        UserChangePasswordRequest request = new UserChangePasswordRequest(wrongPassword, newPassword);

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            userService.changePassword(userId, request);
        });

        // then
        assertEquals("잘못된 비밀번호입니다.",exception.getMessage());
    }
}
