package org.example.expert.domain.todo.service;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TodoAuthServiceTest {

    @InjectMocks
    private TodoAuthorization authorization;

    @Mock
    private TodoRepository todoRepository;

    @Test
    public void Todo의_owner가_userId와_일치하면_예외가_발생하지_않는다() {
        // given
        long todoId = 1L;
        long userId = 1L;

        given(todoRepository.existsByIdAndUserId(todoId, userId)).willReturn(true);

        // when
        authorization.validateOwner(todoId, userId);

        // then
        verify(todoRepository).existsByIdAndUserId(todoId, userId);
    }

    @Test
    public void Todo의_owner가_userId와_일치하지_않으면_에러발생() {
        // given
        long todoId = 1L;
        long userId = 2L;
        given(todoRepository.existsByIdAndUserId(todoId, userId)).willReturn(false);

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> authorization.validateOwner(todoId, userId));
        verify(todoRepository).existsByIdAndUserId(todoId, userId);

        // then
        assertEquals("일정을 생성한 유저만 담당자를 제어 할 수 있습니다.", exception.getMessage());

    }
}
