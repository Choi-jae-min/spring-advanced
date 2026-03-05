package org.example.expert.global.security;

import org.example.expert.global.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class JwtFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void changeUserRole_정보없는_토큰() throws Exception {
        given(jwtUtil.substringToken(any())).willReturn("sometoken");
        given(jwtUtil.extractClaims("sometoken")).willReturn(null);

        mockMvc.perform(patch("/admin/users/{userId}", 1)
                        .header("Authorization", "Bearer fakefakefakefakefakefakefakefakefakefake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."));
    }

    @Test
    void changeUserRole_예상치못한_오류_500() throws Exception {
        // given

        doReturn("sometoken").when(jwtUtil).substringToken(any());
        doThrow(new RuntimeException("예상치 못한 오류")).when(jwtUtil).extractClaims(any());

        // when & then
        mockMvc.perform(patch("/admin/users/{userId}", 1)
                        .header("Authorization", "Bearer fakefakefakefakefakefakefakefake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("요청 처리 중 오류가 발생했습니다."));
    }

}