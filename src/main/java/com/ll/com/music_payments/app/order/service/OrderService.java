package com.ll.com.music_payments.app.order.service;

import com.ll.com.music_payments.app.cart.entity.CartItem;
import com.ll.com.music_payments.app.cart.service.CartService;
import com.ll.com.music_payments.app.cash.service.CashService;
import com.ll.com.music_payments.app.member.entity.Member;
import com.ll.com.music_payments.app.member.service.MemberService;
import com.ll.com.music_payments.app.order.entity.Order;
import com.ll.com.music_payments.app.order.entity.OrderItem;
import com.ll.com.music_payments.app.order.repository.OrderRepository;
import com.ll.com.music_payments.app.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {


    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final MemberService memberService;

    @Transactional
    public Order createFromCart(Member buyer) {
        // 해당 회원의 장바구니 아이템들을 전부 가져온다.
        // 특정 장바구니의 상품이 판매 불가능하면 삭제
        // 특정 장바구니의 상품이 판매 가능하면 주문 품목으로 이동 후 삭제

        // buyer가 담은 장바구니 아이템 가져오기
        List<CartItem> cartItems = cartService.getItemsByBuyer(buyer);

        List<OrderItem> orderItems = new ArrayList<>();

        for( CartItem cartItem : cartItems ) {
            Product product = cartItem.getProduct();

            if( product.isOrderable() ) {
                orderItems.add(new OrderItem(product)); // 상품 주문 가능하면 주문 품목에 추가
            }

            cartService.removeItem(cartItem); // 장바구니 아이 품목 제거하기
        }

        return create(buyer, orderItems);
    }

    @Transactional
    public Order create(Member buyer, List<OrderItem> orderItems) {

        Order order = Order.builder()
                .buyer(buyer)
                .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        // 주문 품목으로부터 이름을 만든다.
        order.makeName();

        orderRepository.save(order);

        return order;
    }

    @Transactional
    public void payByRestCashOnly(Order order) { // 오직 캐시로 결제할 경우

        Member buyer = order.getBuyer(); // 주문한 사람

        long restCash = buyer.getRestCash(); // 보유한 캐시

        int payPrice = order.calculatePayPrice(); // 결제 금액

        if( payPrice > restCash ) { // 결제 금액 > 보유한 캐시
            throw new RuntimeException("예치금이 잔액이 부족합니다.");
        }

        memberService.addCash(buyer, payPrice * -1, "주문결제_예치금결제");

        order.setPaymentDone();
        orderRepository.save(order);
    }

    @Transactional
    public void refund(Order order) {
        int payPrice = order.getPayPrice();
        memberService.addCash(order.getBuyer(), payPrice, "주문환불__예치금환불");

        order.setRefundDone();
        orderRepository.save(order);
    }

    public Optional<Order> findForPrintById(long id) {

        return findById(id);
    }

    private Optional<Order> findById(long id) {

        return orderRepository.findById(id);
    }


    public boolean authorCanSee(Member author, Order order) {

        return author.getId().equals(order.getBuyer().getId());
    }

    @Transactional
    public void payByTossPayments(Order order) {
        Member buyer = order.getBuyer();
        int payPrice = order.calculatePayPrice();

        // 충전했다가 결제하는 이유는 예치금에 편입시키기 위해
        memberService.addCash(buyer, payPrice, "주문결제충전__토스페이먼츠");
        memberService.addCash(buyer, payPrice * -1, "주문결제__토스페이먼츠");

        order.setPaymentDone();
        orderRepository.save(order);
    }

    public boolean authorCanPayment(Member author, Order order) {
        return authorCanSee(author, order);
    }
}
