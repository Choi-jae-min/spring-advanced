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
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    private String adminToken;
    private String userToken;

    private Long userId;
    private Long adminUserId;

    @BeforeEach
    void setUp() {
        AuthUser adminAuthUser = new AuthUser(1L, "email", UserRole.ADMIN);
        AuthUser authuser = new AuthUser(2L, "user", UserRole.USER);
        User adminUser = User.fromAuthUser(adminAuthUser);
        User user = User.fromAuthUser(authuser);
        userRepository.save(adminUser);
        userRepository.save(user);
        userId = user.getId();
        adminUserId = adminUser.getId();

        adminToken = jwtUtil.createToken(adminAuthUser.getId(),adminAuthUser.getEmail(),adminAuthUser.getUserRole());
        userToken = jwtUtil.createToken(user.getId(),user.getEmail(),user.getUserRole());
    }

    @Test
    void changeUserRole_성공() throws Exception {
        String requesstBody = """
                {
                    "role" : "ADMIN"
                }
                """;

        mockMvc.perform(patch("/admin/users/{userId}", userId)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requesstBody))
                .andExpect(status().isOk());
    }
    @Test
    void changeUserRole_권한_부족() throws Exception {
        String requesstBody = """
                {
                    "role" : "ADMIN"
                }
                """;

        mockMvc.perform(patch("/admin/users/{userId}", userId)
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requesstBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeUserRole_토큰_누락() throws Exception {
        mockMvc.perform(patch("/admin/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }

    @Test
    void changeUserRole_토큰_만료() throws Exception {
        String expiredToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1OCIsImVtYWlsIjoidXNlcjRAdGVzdC5jb20iLCJ1c2VyUm9sZSI6IkFETUlOIiwiZXhwIjoxNzcyNjg2NjAzLCJpYXQiOjE3NzI2ODY2MDN9.AZIkZ_TWnWtUy1Jim66p0pxj9avJrGJ4WClDtJybGqQ";

        mockMvc.perform(patch("/admin/users/{userId}", userId)
                        .header("Authorization", expiredToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }

    @Test
    void changeUserRole_토큰_변조() throws Exception {
        mockMvc.perform(patch("/admin/users/{userId}", userId)
                        .header("Authorization", "Bearer fakefakefakefakefakefakefakefakefakefake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }
}