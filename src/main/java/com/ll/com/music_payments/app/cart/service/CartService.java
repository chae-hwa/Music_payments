package com.ll.com.music_payments.app.cart.service;

import com.ll.com.music_payments.app.cart.entity.CartItem;
import com.ll.com.music_payments.app.cart.repository.CartItemRepository;
import com.ll.com.music_payments.app.member.entity.Member;
import com.ll.com.music_payments.app.order.entity.OrderItem;
import com.ll.com.music_payments.app.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {
    private final CartItemRepository cartItemRepository;

    @Transactional
    public CartItem addItem(Member buyer, Product product) {
        CartItem oldCartItem = cartItemRepository.findByBuyerIdAndProductId(buyer.getId(), product.getId()).orElse(null);

        if (oldCartItem != null) {
            return oldCartItem;
        }

        CartItem cartItem = CartItem.builder()
                .buyer(buyer)
                .product(product)
                .build();

        cartItemRepository.save(cartItem);

        return cartItem;
    }

    @Transactional
    public boolean removeItem(Member buyer, Product product) {
        CartItem oldCartItem = cartItemRepository.findByBuyerIdAndProductId(buyer.getId(), product.getId()).orElse(null);

        if (oldCartItem != null) {
            cartItemRepository.delete(oldCartItem);
            return true;
        }

        return false;
    }

    public boolean hasItem(Member buyer, Product product) {
        return cartItemRepository.existsByBuyerIdAndProductId(buyer.getId(), product.getId());
    }

    public List<CartItem> getItemsByBuyer(Member buyer) {
        return cartItemRepository.findAllByBuyerId(buyer.getId());
    }

    @Transactional
    public void removeItem(CartItem cartItem) {
        cartItemRepository.delete(cartItem);
    }

    public Optional<CartItem> findItemById(long id) {
        return cartItemRepository.findById(id);

    }

    public boolean actorCanDelete(Member buyer, CartItem cartItem) {
        return buyer.getId().equals(cartItem.getBuyer().getId());
    }
}