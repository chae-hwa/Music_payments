package com.ll.com.music_payments.app.member.service;

import com.ll.com.music_payments.app.base.dto.RsData;
import com.ll.com.music_payments.app.cash.entity.CashLog;
import com.ll.com.music_payments.app.cash.service.CashService;
import com.ll.com.music_payments.app.member.entity.Member;
import com.ll.com.music_payments.app.member.exception.AlreadyJoinException;
import com.ll.com.music_payments.app.member.repository.MemberRepository;
import com.ll.com.music_payments.util.Ut;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CashService cashService;

    public Member join(String username, String password, String email) {
        if (memberRepository.findByUsername(username).isPresent()) {
            throw new AlreadyJoinException();
        }

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();

        memberRepository.save(member);

        return member;
    }

    @Transactional(readOnly = true)
    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    @Transactional
    public RsData<AddCashRsDataBody> addCash(Member member, long price, String eventType) {

        CashLog cashLog = cashService.addCash(member, price, eventType);

        // 새 캐시 금액 = 기존 보유 캐시 + 캐시 금액
        long newRestCash = member.getRestCash() + cashLog.getPrice();
        member.setRestCash(newRestCash);
        memberRepository.save(member);

        return RsData.of(
                "S-1",
                "성공",
                new AddCashRsDataBody(cashLog, newRestCash)
        );
    }

    @Data
    @AllArgsConstructor
    public static class AddCashRsDataBody {
        CashLog cashLog;
        long newRestCash;
    }

    public long getRestCash(Member member) {
        return member.getRestCash();
    }
}