package com.ll.com.music_payments.app.order.controller;


import com.ll.com.music_payments.app.member.entity.Member;
import com.ll.com.music_payments.app.order.entity.Order;
import com.ll.com.music_payments.app.order.exception.AuthorCanNotSeeOrderException;
import com.ll.com.music_payments.app.order.service.OrderService;
import com.ll.com.music_payments.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String showDetail(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id, Model model){

        Order order = orderService.findForPrintById(id).get();

        Member author = memberContext.getMember();

        if( orderService.authorCanSee(author, order) == false ){
            throw new AuthorCanNotSeeOrderException();
        }

        model.addAttribute("order", order);

        return "order/detail";
    }
}
