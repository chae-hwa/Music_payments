package com.ll.com.music_payments.controller;

import com.ll.com.music_payments.app.member.controller.MemberController;
import com.ll.com.music_payments.app.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class MemberControllerTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 폼")
    void t1() throws Exception {
        // WHEN : GET /member/join으로 요청하면
        ResultActions resultActions = mvc
                .perform(get("/member/join"))
                .andDo(print());

        // THEN : MemberController의 showJoin 메서드에 200 상태 코드가 나와야 된다.
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showJoin"))
                .andExpect(content().string(containsString("회원가입")));
    }

    @Test
    @DisplayName("회원가입")
    void t2() throws Exception {
        // WHEN : POST /member/join으로 요청하면 (해당 정보로)
        ResultActions resultActions = mvc
                .perform(post("/member/join")
                        .with(csrf())
                        .param("username", "user999")
                        .param("password", "1234")
                        .param("email", "user999@test.com")
                )
                .andDo(print());

        // THEN : MemberController의 join 메서드에 300 상태 코드로 리다이렉션 되어야 한다.
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(redirectedUrlPattern("/member/login?msg=**"));

        assertThat(memberService.findByUsername("user999").isPresent()).isTrue();
    }
}
