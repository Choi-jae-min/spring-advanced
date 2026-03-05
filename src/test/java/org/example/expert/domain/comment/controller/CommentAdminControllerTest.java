package org.example.expert.domain.comment.controller;

import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String token;

    private Long commentId;

    @BeforeEach
    void setUp() {
        AuthUser authUser = new AuthUser(1L, "email", UserRole.ADMIN);
        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo("Title","contents","SUNNY",user);
        todoRepository.save(todo);
        Comment comment = new Comment("comment_title",user,todo);
        commentRepository.save(comment);
        commentId = comment.getId();
        token = jwtUtil.createToken(authUser.getId(),authUser.getEmail(),authUser.getUserRole());
    }

    @Test
    void deleteTodo_성공() throws Exception {
        mockMvc.perform(delete("/admin/comments/{commentId}", commentId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}