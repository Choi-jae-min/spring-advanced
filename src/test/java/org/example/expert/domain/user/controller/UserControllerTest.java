package org.example.expert.domain.user.controller;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.global.security.PasswordEncoder;
import org.example.expert.global.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    String token;

    Long userId;
    @BeforeEach
    void setUp() {
        String oldPassword = passwordEncoder.encode("password");
        User user = new User("email" ,oldPassword ,UserRole.ADMIN);
        userRepository.save(user);
        userId = user.getId();
        token = jwtUtil.createToken(user.getId(),user.getEmail(),user.getUserRole());
    }

    @Test
    void getUser_성공() throws Exception {
        mockMvc.perform(get("/users/{userId}", userId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_성공() throws Exception {
        String requestBody = """
                {
                    "oldPassword" : "password",
                    "newPassword" : "Password1234"
                }
                """;

        mockMvc.perform(put("/users", userId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }


}