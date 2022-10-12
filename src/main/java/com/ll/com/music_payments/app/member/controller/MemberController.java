package com.ll.com.music_payments.app.member.controller;

import com.ll.com.music_payments.app.member.form.JoinForm;
import com.ll.com.music_payments.app.member.service.MemberService;
import com.ll.com.music_payments.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PreAuthorize("isAnonymous()") // 비로그인 상태
    @GetMapping("/login")
    public String showLogin(HttpServletRequest request) {

        String uri = request.getHeader("Referer");

        if(uri != null && !uri.contains("/member/login")) { // uri이 null 이 아니고 /member/login이 아닐 때
            request.getSession().setAttribute("prevPage", uri); // 세션을 얻고 세션에 값을 저장하겠다.
        }

        return "/member/login";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {

        return "/member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm) {

        memberService.join(joinForm.getUsername(), joinForm.getPassword(), joinForm.getEmail());

        return "redirect:/member/login?msg=" + Ut.url.encode("회원가입이 완료되었습니다.");
    }
}
