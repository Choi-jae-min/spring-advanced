package org.example.expert.domain.manager.controller;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String token;

    private Long userId;

    private Long todoId;

    @BeforeEach
    void setUp() {
        AuthUser authAdminUser = new AuthUser(1L, "email", UserRole.ADMIN);
        User adminUser = User.fromAuthUser(authAdminUser);
        userRepository.save(adminUser);

        AuthUser authUser = new AuthUser(2L, "user@email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        userRepository.save(user);

        userId = adminUser.getId();

        Todo todo = new Todo("title", "contents","SUNNY" , adminUser);
        todoRepository.save(todo);
        todoId = todo.getId();

        token = jwtUtil.createToken(adminUser.getId(),adminUser.getEmail(),adminUser.getUserRole());
    }

    @Test
    void manager저장_성공() throws Exception {
        String requesstBody = """
                {
                    "managerUserId" : "2"
                }
                """;

        mockMvc.perform(post("/todos/{todoId}/managers", todoId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requesstBody))
                .andExpect(status().isOk());
    }

    @Test
    void getmanager_성공() throws Exception {
        mockMvc.perform(get("/todos/{todoId}/managers", todoId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void delete_manager_성공() throws Exception {
        User user = userRepository.findById(userId).get();
        Todo todo = todoRepository.findById(todoId).get();
        Manager manager = new Manager(user, todo);

        managerRepository.save(manager);
        mockMvc.perform(delete("/todos/{todoId}/managers/{managerId}", todoId , manager.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}