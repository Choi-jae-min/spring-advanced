package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TodoAuthorization {

    private final TodoRepository todoRepository;

    public void validateOwner(Long todoId, Long userId) {
        boolean isOwner = todoRepository.existsByIdAndUserId(todoId, userId);
        if (!isOwner) {
            throw new InvalidRequestException("일정을 생성한 유저만 담당자를 제어 할 수 있습니다.");
        }
    }
}
