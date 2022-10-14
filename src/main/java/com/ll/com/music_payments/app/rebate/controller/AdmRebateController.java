package com.ll.com.music_payments.app.rebate.controller;

import com.ll.com.music_payments.util.Ut;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/adm/rebate")
public class AdmRebateController {

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/makeData")
    public String showMakeData() {

        return "adm/rebate/makeData";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/makeDate")
    @ResponseBody
    public String makeData(String yearMonth) {
        int monthEndDay = Ut.date.getEndDayOf(yearMonth);

        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay);
        LocalDateTime fromDate = Ut.date.parse(fromDateStr);
        LocalDateTime toDate = Ut.date.parse(toDateStr);

        return "fromDateStr : %s<br>toDateStr : %s".formatted(fromDateStr, toDateStr);
    }

}
