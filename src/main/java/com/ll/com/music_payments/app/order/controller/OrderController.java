package com.ll.com.music_payments.app.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.com.music_payments.app.member.entity.Member;
import com.ll.com.music_payments.app.member.service.MemberService;
import com.ll.com.music_payments.app.order.entity.Order;
import com.ll.com.music_payments.app.order.exception.AuthorCanNotPayOrderException;
import com.ll.com.music_payments.app.order.exception.AuthorCanNotSeeOrderException;
import com.ll.com.music_payments.app.order.exception.OrderIdNotMatchedException;
import com.ll.com.music_payments.app.order.exception.OrderNotEnoughRestCashException;
import com.ll.com.music_payments.app.order.service.OrderService;
import com.ll.com.music_payments.security.dto.MemberContext;
import com.ll.com.music_payments.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResponseErrorHandler;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String showDetail(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id, Model model){

        Order order = orderService.findForPrintById(id).get();

        Member author = memberContext.getMember();

        long restCash = memberService.getRestCash(author);

        if( orderService.authorCanSee(author, order) == false ){
            throw new AuthorCanNotSeeOrderException();
        }

        model.addAttribute("order", order);
        model.addAttribute("authorRestCash", restCash);

        return "order/detail";
    }

    // 예치금 사용 후 결제하기 눌렀을 때 이동할 페이지
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/payByRestCashOnly")
    public String payByRestCashOnly(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id) {
        Order order = orderService.findForPrintById(id).get();

        Member author = memberContext.getMember();

        long restCash = memberService.getRestCash(author);

        if( orderService.authorCanPayment(author, order) == false) {
            throw new AuthorCanNotPayOrderException();
        }

        orderService.payByRestCashOnly(order);

        return "redirect:/order/%d?msg=%s".formatted(order.getId(), Ut.url.encode("예치금으로 결제했습니다."));

    }


    // 토스 페이먼츠 백엔드 코드 시작
    @PostConstruct
    private void init() {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        });
    }
    private final String SECRET_KEY = "test_sk_Lex6BJGQOVDBRnp2YOO8W4w2zNbg";

    @RequestMapping("/{id}/success")
    public String confirmPayment(
            @PathVariable long id,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            Model model,
            @AuthenticationPrincipal MemberContext memberContext
    ) throws Exception {

        Order order = orderService.findForPrintById(id).get();

        long orderIdInputed = Long.parseLong(orderId.split("__")[1]);

        if ( id != orderIdInputed ) {
            throw new OrderIdNotMatchedException();
        }

        HttpHeaders headers = new HttpHeaders();
        // headers.setBasicAuth(SECRET_KEY, ""); // spring framework 5.2 이상 버전에서 지원
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        Member actor = memberContext.getMember();
        long restCash = memberService.getRestCash(actor);
        long payPriceRestCash = order.calculatePayPrice() - amount;

        if (payPriceRestCash > restCash) {
            throw new OrderNotEnoughRestCashException();
        }

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {

            orderService.payByTossPayments(order, payPriceRestCash);

            return "redirect:/order/%d?msg=%s".formatted(order.getId(), Ut.url.encode("결제가 완료되었습니다."));
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());
            return "order/fail";
        }
    }

    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "order/fail";
    }
    // 토스 페이먼츠 백엔드 코드 끝
}
