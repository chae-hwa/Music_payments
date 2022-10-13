package com.ll.com.music_payments.app.order.entity;

import com.ll.com.music_payments.app.base.entity.BaseEntity;
import com.ll.com.music_payments.app.member.entity.Member;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Table(name = "product_order")
public class Order extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    private Member buyer;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem) {
        orderItem.setOrder(this);

        orderItems.add(orderItem);
    }

    public int calculatePayPrice() {

        int payPrice = 0;

        for( OrderItem orderItem : orderItems ) {
            payPrice += orderItem.getSalePrice();
        }

        return payPrice;
    }

    public void setPaymentDone() {

        for (OrderItem orderItem : orderItems) {
            orderItem.setPaymentDone();
        }
    }

    public int getPayPrice() {
        
        int payPrice = 0;
        
        for( OrderItem orderItem : orderItems ) {
            payPrice += orderItem.getPayPrice();
        }
        
        return payPrice;
    }

    public void setRefundDone() {

        for (OrderItem orderItem : orderItems) {
            orderItem.setRefundDone();
        }
    }

    // 주문 이름 가져오기
    public String getName() {
        String name = orderItems.get(0).getProduct().getSubject();

        if ( orderItems.size() > 1 ) {
            name += " 외 %d곡".formatted(orderItems.size() - 1);
        }

        return name;
    }
}
