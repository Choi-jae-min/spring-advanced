package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.request.TodoUpdateRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;
    @Mock
    private WeatherClient weatherClient;
    @InjectMocks
    private TodoService todoService;

    @Test
    public void Todo를_성공적으로_생성한다(){
        //given
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("할일 제목", "어쩌구 저쩌구");

        User user = User.fromAuthUser(authUser);
        String weather = "SUNNY";
        Todo todo = new Todo("할일 제목", "어쩌구 저쩌구", weather, user);
        given(todoRepository.save(any())).willReturn(todo);
        given(weatherClient.getTodayWeather()).willReturn(weather);

        //when
        TodoSaveResponse result = todoService.saveTodo(authUser , todoSaveRequest);

        //then
        assertNotNull(result);
        verify(todoRepository).save(any());
    }

    @Test
    public void Todo_단건_조회에_성공한다(){
        //given
        Long todoId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.of(todo));

        //when
        TodoResponse result = todoService.getTodo(todoId);

        //then
        assertEquals(todo.getId(), result.getId());
        assertEquals(todo.getTitle(), result.getTitle());
    }

    @Test
    public void TodoId로_Todo_단건_조회에_성공한다(){
        //given
        long todoId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.of(todo));

        //when
        Todo result = todoService.getTodoByIdWithUser(todoId);

        //then
        assertEquals(todo.getId(), result.getId());
        assertEquals(todo.getTitle(), result.getTitle());
    }

    @Test
    public void Todo_목록_조회에_성공한다(){
        //given
        int page = 1;
        int size = 10;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        // to-do 2개 만들어서
        Todo todo = new Todo("Title1", "Contents", "Sunny", user);
        Todo todo2 = new Todo("Title2", "Contents", "Sunny", user);
        // Pageable 만들고 list에 담고
        Pageable pageable = PageRequest.of(page-1, size);
        List<Todo> todoList = List.of(todo, todo2);

        // page 객체로 만들기
        Page<Todo> todoPage = new PageImpl<>(todoList, pageable, todoList.size());
        given(todoRepository.findAllByOrderByModifiedAtDesc(pageable)).willReturn(todoPage);

        //when
        Page<TodoResponse> result = todoService.getTodos(page ,size);

        //then
        assertEquals(todoPage.getTotalElements(), result.getTotalElements());
        assertEquals(todoPage.getTotalPages(), result.getTotalPages());
    }

    @Test
    public void Todo_수정에_성공한다(){
        //given
        Long todoId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        TodoUpdateRequest todoUpdateRequest = new TodoUpdateRequest("newTitle", "newContents");
        given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.of(todo));

        //when
        TodoResponse result = todoService.updateTodo(todoId,todoUpdateRequest);

        //then
        assertEquals(todoUpdateRequest.getTitle(), result.getTitle());
        assertEquals(todoUpdateRequest.getContents(), result.getContents());
    }
}
