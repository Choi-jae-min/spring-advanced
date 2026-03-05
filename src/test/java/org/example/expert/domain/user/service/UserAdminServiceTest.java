package org.example.expert.domain.user.service;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserAdminServiceTest {

    @InjectMocks
    private UserAdminService userAdminService;
    @Mock
    private UserRepository userRepository;

    @Test
    public void 유저권한_수정중_유저를_찾지못해_에러발생() {
        //given
        Long userId = 1L;

        given(userRepository.findById(userId))
                .willThrow(new InvalidRequestException("User not found"));

        UserRoleChangeRequest request = new UserRoleChangeRequest("USER");

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            userAdminService.changeUserRole(userId, request);
        });

        //then
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void 유저권한_수정_성공() {
        //given
        Long userId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        UserRoleChangeRequest request = new UserRoleChangeRequest("USER");

        //when
        userAdminService.changeUserRole(userId, request);

        //then
        verify(userRepository).findById(userId);
    }
}
