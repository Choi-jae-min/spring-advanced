package org.example.expert.domain.manager.service;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private UserService userService;
    @Mock
    private TodoService todoService;
    @InjectMocks
    private ManagerService managerService;

    // NPE -> InvalidRequestException 을 반환 함으로 IRE 로 변경
    @Test
    public void manager_목록_조회_시_Todo가_없다면_IRE_에러를_던진다() {
        // given
        long todoId = 1L;
        given(todoService.getTodoByIdWithUser(todoId)).willThrow(new InvalidRequestException("Todo not found"));

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.getManagers(todoId));
        // 서비스에서 Todo not found 를 message로 설정 했음으로 통일.
        assertEquals("Todo not found", exception.getMessage());
    }

//    @Test -> interceptor에서 검증
//    void to-do의_user가_null인_경우_예외가_발생한다() {}

    @Test // 테스트코드 샘플
    public void manager_목록_조회에_성공한다() {
        // given
        long todoId = 1L;

        AuthUser authUser = new AuthUser(1L, "user@email.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo("title", "contents", "weather", user);

        AuthUser managerAuthUser = new AuthUser(2L, "manager@email.com", UserRole.USER);
        User managerUser = User.fromAuthUser(managerAuthUser);
        Manager manager = new Manager(managerUser, todo);

        given(todoService.getTodoByIdWithUser(todoId)).willReturn(todo);
        given(managerRepository.findByTodoIdWithUser(todo.getId())).willReturn(List.of(manager));

        // when
        List<ManagerResponse> result = managerService.getManagers(todoId);

        // then
        assertEquals(1, result.size());
        assertEquals(managerUser.getEmail(), result.get(0).getUser().getEmail());
    }

    @Test // 테스트코드 샘플
    void manager가_정상적으로_등록된다() {
        // given
        long userId = 1L;
        long todoId = 1L;
        long managerUserId = 2L;

        AuthUser authUser = new AuthUser(userId, "user@email.com", UserRole.USER);
        User todoOwner = User.fromAuthUser(authUser);

        AuthUser managerAuthUser = new AuthUser(managerUserId, "manager@email.com", UserRole.USER);
        User managerUser = User.fromAuthUser(managerAuthUser);

        Todo todo = new Todo("title", "contents", "weather", todoOwner);
        ManagerSaveRequest request = new ManagerSaveRequest(managerUserId);

        Manager savedManager = new Manager(managerUser, todo);

        given(userService.getUserById(managerUserId)).willReturn(managerUser);
        given(todoService.getTodoByIdWithUser(todoId)).willReturn(todo);
        given(managerRepository.save(any())).willReturn(savedManager);

        // when
        ManagerSaveResponse result = managerService.saveManager(userId, todoId, request);

        // then
        assertNotNull(result);
        assertEquals(managerUser.getEmail(), result.getUser().getEmail());
        verify(managerRepository, times(1)).save(any());
    }

    @Test
    public void 일정_작성자가_본인을_담당자로_등록시_에러발생() {
        // given
        long userId = 1L;
        long todoId = 1L;

        AuthUser authUser = new AuthUser(userId, "user@email.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        ManagerSaveRequest request = new ManagerSaveRequest(userId); // 본인 id

        given(userService.getUserById(userId)).willReturn(user);

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            managerService.saveManager(userId, todoId, request);
        });

        // then
        assertEquals("일정 작성자는 본인을 담당자로 등록할 수 없습니다.", exception.getMessage());
        verify(managerRepository, never()).save(any());
    }

    @Test
    public void manager를_정상적으로_삭제한다() {
        // given
        long todoId = 1L;
        long managerId = 1L;

        AuthUser authUser = new AuthUser(1L, "user@email.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo("title", "contents", "weather", user);
        Manager manager = new Manager(user, todo);

        given(todoService.getTodoByIdWithUser(todoId)).willReturn(todo);
        given(managerRepository.findById(managerId)).willReturn(Optional.of(manager));

        // when
        managerService.deleteManager(todoId, managerId);

        // then
        verify(managerRepository, times(1)).delete(manager);
    }

    @Test
    public void 존재하지_않는_manager_삭제시_에러발생() {
        // given
        long todoId = 1L;
        long managerId = 1L;

        AuthUser authUser = new AuthUser(1L, "user@email.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo("title", "contents", "weather", user);

        given(todoService.getTodoByIdWithUser(todoId)).willReturn(todo);
        given(managerRepository.findById(managerId)).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            managerService.deleteManager(todoId, managerId);
        });

        // then
        assertEquals("Manager not found", exception.getMessage());
        verify(managerRepository, never()).delete(any());
    }

    @Test
    public void 해당_일정에_등록되지_않은_manager_삭제시_에러발생() {
        // given
        long todoId = 1L;
        long managerId = 1L;

        AuthUser authUser = new AuthUser(1L, "user@email.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        Todo todo = new Todo("title", "contents", "weather", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        Todo otherTodo = new Todo("other title", "other contents", "weather", user);
        ReflectionTestUtils.setField(otherTodo, "id", 2L);

        Manager manager = new Manager(user, otherTodo);

        given(todoService.getTodoByIdWithUser(todoId)).willReturn(todo);
        given(managerRepository.findById(managerId)).willReturn(Optional.of(manager));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            managerService.deleteManager(todoId, managerId);
        });

        // then
        assertEquals("해당 일정에 등록된 담당자가 아닙니다.", exception.getMessage());
        verify(managerRepository, never()).delete(any());
    }

    @Test
    public void manager의_todo가_null인_경우_에러발생() {
        // given
        long todoId = 1L;
        long managerId = 1L;

        AuthUser authUser = new AuthUser(1L, "user@email.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        Todo todo = new Todo("title", "contents", "weather", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        Manager manager = new Manager(user, null);

        given(todoService.getTodoByIdWithUser(todoId)).willReturn(todo);
        given(managerRepository.findById(managerId)).willReturn(Optional.of(manager));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            managerService.deleteManager(todoId, managerId);
        });

        // then
        assertEquals("해당 일정에 등록된 담당자가 아닙니다.", exception.getMessage());
    }
}
