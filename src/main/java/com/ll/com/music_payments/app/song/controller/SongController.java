package com.ll.com.music_payments.app.song.controller;

import com.ll.com.music_payments.app.member.entity.Member;
import com.ll.com.music_payments.app.song.entity.Song;
import com.ll.com.music_payments.app.song.exception.ActorCanNotModifyException;
import com.ll.com.music_payments.app.song.exception.ActorCanNotSeeException;
import com.ll.com.music_payments.app.song.form.SongForm;
import com.ll.com.music_payments.app.song.service.SongService;
import com.ll.com.music_payments.security.dto.MemberContext;
import com.ll.com.music_payments.util.Ut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/song")
@Slf4j
public class SongController {

    private final SongService songService;

    // 음원 업로드 폼
    @PreAuthorize("isAuthenticated()") // 로그인 상태
    @GetMapping("/create")
    public String showCreate() {

        return "song/create";
    }

    // 음원 업로드
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@AuthenticationPrincipal MemberContext memberContext, @Valid SongForm songForm) {

        Member author = memberContext.getMember();
        Song song = songService.create(author, songForm.getSubject(), songForm.getContent());

        return "redirect:/song/" + song.getId() + "?msg=" + Ut.url.encode("%d번 음원이 생성되었습니다.".formatted(song.getId()));
    }

    // 음원 수정 폼
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String showModify(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id, Model model) {

        Song song = songService.findForPrintById(id).get();

        Member author = memberContext.getMember();

        if ( songService.authorCanModify(author, song) == false ) {
            throw new ActorCanNotModifyException();
        }

        model.addAttribute("song", song);

        return "song/modify";
    }

    // 음원 수정
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/modify")
    public String modify(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id, @Valid SongForm songForm) {

        Song song = songService.findById(id).get();

        Member author = memberContext.getMember();

        if ( songService.authorCanModify(author, song) == false ) {
            throw new ActorCanNotModifyException();
        }

        songService.modify(song, songForm.getSubject(), songForm.getContent());

        return "redirect:/song/" + song.getId() + "?msg=" + Ut.url.encode("%d번 음원이 생성되었습니다.".formatted(song.getId()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String detail(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long id, Model model) {

        Song song = songService.findForPrintById(id).get();

        Member author = memberContext.getMember();

        if(songService.authorCanModify(author, song) ) {
            throw new ActorCanNotSeeException();
        }

        model.addAttribute("song", song);

        return "song/detail";
    }
}
