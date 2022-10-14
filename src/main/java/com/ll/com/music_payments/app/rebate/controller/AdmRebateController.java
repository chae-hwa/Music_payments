package com.ll.com.music_payments.app.rebate.controller;

import com.ll.com.music_payments.app.base.dto.RsData;
import com.ll.com.music_payments.app.rebate.entity.RebateOrderItem;
import com.ll.com.music_payments.app.rebate.service.RebateService;
import com.ll.com.music_payments.util.Ut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/adm/rebate")
@Slf4j
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

        RsData makeDateRsData = rebateService.makeDate(yearMonth);

        String redirect = makeDateRsData.addMsgToUrl("redirect:/adm/rebate/rebateOrderItemList?yearMonth=" + yearMonth);

        return redirect;
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/rebateOne/{orderItemId}")
    @ResponseBody
    public String rebateOne(@PathVariable long orderItemId, HttpServletRequest req) {
        RsData rebateRsData = rebateService.rebate(orderItemId);

        String referer = req.getHeader("Referer");
        String yearMonth = Ut.url.getQueryParamValue(referer, "yearMonth", "");

        String redirect = "redirect:/adm/rebate/rebateOrderItemList?yearMonth=" + yearMonth;

        redirect = rebateRsData.addMsgToUrl(redirect);

        return redirect;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/rebate")
    public String rebate(String ids, HttpServletRequest req) {

        String[] idsArr = ids.split(",");

        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    rebateService.rebate(id);
                });

        String referer = req.getHeader("Referer");
        String yearMonth = Ut.url.getQueryParamValue(referer, "yearMonth", "");

        String redirect = "redirect:/adm/rebate/rebateOrderItemList?yearMonth=" + yearMonth;
        redirect += "&msg=" + Ut.url.encode("%d건의 정산품목을 정산처리하였습니다.".formatted(idsArr.length));

        return redirect;
    }
}
