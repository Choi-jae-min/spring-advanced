package org.example.expert.domain.todo.controller;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.global.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String token;

    @BeforeEach
    void setUp() {
        // todo setup
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo("Title","contents","SUNNY",user);
        todoRepository.save(todo);

        token = jwtUtil.createToken(authUser.getId(),authUser.getEmail(),authUser.getUserRole());
    }

    @Test
    void saveTodo_성공() throws Exception {
        String requestBody = """
                {
                "title": "Title",
                "contents": "contents"
                }
                """;

        mockMvc.perform(post("/todos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void getTodo성공() throws Exception {
        mockMvc.perform(get("/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                )
                .andExpect(status().isOk());
    }

    @Test
    void getTodos성공() throws Exception {
        mockMvc.perform(get("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("page", "1")
                        .param("size", "10")
                )
                .andExpect(status().isOk());
    }

    @Test
    void updateTodo_성공() throws Exception {
        String requestBody = """
                {
                "title": "Title1",
                "contents": "contents1"
                }
                """;

        mockMvc.perform(patch("/todos/1")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title1"))
                .andExpect(jsonPath("$.contents").value("contents1"))
                .andExpect(jsonPath("$.id").value(1L));
    }
}