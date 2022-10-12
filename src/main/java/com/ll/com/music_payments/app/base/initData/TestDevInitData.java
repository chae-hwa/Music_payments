package com.ll.com.music_payments.app.base.initData;

import com.ll.com.music_payments.app.member.service.MemberService;
import com.ll.com.music_payments.app.song.service.SongService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

public class TestDevInitData implements InitDataBefore{

    @Bean
    CommandLineRunner initDate(MemberService memberService, SongService songService) {
        return args -> {
            before(memberService, songService);
        };
    }
}
