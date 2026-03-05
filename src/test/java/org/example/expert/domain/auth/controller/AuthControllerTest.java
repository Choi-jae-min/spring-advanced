package org.example.expert.domain.auth.controller;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.global.security.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void signup_성공() throws Exception {
        String requestBody = """
                {
                "email": "user1@email.com",
                "password": "password",
                "userRole": "USER"
                }
                """;

        mockMvc.perform(post("/auth//signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void signin_성공() throws Exception {
        String encodedPassword = passwordEncoder.encode("password");

        User user = new User("user1@email.com" ,encodedPassword, UserRole.USER);

        userRepository.save(user);


        String requestBody = """
                {
                "email": "user1@email.com",
                "password": "password"
                }
                """;

        mockMvc.perform(post("/auth//signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

}