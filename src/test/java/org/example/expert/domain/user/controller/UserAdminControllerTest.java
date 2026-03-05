package org.example.expert.domain.user.controller;

import org.example.expert.domain.common.dto.AuthUser;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String token;

    private Long user1Id;

    @BeforeEach
    void setUp() {
        AuthUser authUser = new AuthUser(1L, "email", UserRole.ADMIN);
        AuthUser authUser1 = new AuthUser(2L, "user", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        User user1 = User.fromAuthUser(authUser1);
        userRepository.save(user);
        userRepository.save(user1);
        user1Id = user1.getId();

        token = jwtUtil.createToken(authUser.getId(),authUser.getEmail(),authUser.getUserRole());
    }

    @Test
    void changeUserRole_성공() throws Exception {
        String requesstBody = """
                {
                    "role" : "ADMIN"
                }
                """;

        mockMvc.perform(patch("/admin/users/{userId}", user1Id)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requesstBody))
                .andExpect(status().isOk());
    }
}