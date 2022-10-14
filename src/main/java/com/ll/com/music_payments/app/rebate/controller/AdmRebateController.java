package com.ll.com.music_payments.app.rebate.controller;

import com.ll.com.music_payments.app.rebate.entity.RebateOrderItem;
import com.ll.com.music_payments.app.rebate.service.RebateService;
import com.ll.com.music_payments.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

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

        return "redirect:/adm/rebate/rebateOrderItemList?yearMonth=" + yearMonth + "&msg=" + Ut.url.encode("정산데이터가 성공적으로 생성되었습니다.");
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/rebateOrderItemList")
    public String showRebateOrderItemList(String yearMonth, Model model) {
        if(yearMonth == null){
            yearMonth = "2022-10";
        }

        List<RebateOrderItem> items = rebateService.findRebateOrderItemsByPayDateIn(yearMonth);

        model.addAttribute("items", items);

        return "adm/rebate/rebateOrderItemList";
    }
}
