package com.ll.com.music_payments.app.rebate.controller;

import com.ll.com.music_payments.app.rebate.service.RebateService;
import com.ll.com.music_payments.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/adm/rebate")
public class AdmRebateController {

    private final RebateService rebateService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/makeData")
    public String showMakeData() {

        return "adm/rebate/makeData";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/makeData")
    @ResponseBody
    public String makeData(String yearMonth) {

        rebateService.makeDate(yearMonth);

        return "성공";
    }

}
