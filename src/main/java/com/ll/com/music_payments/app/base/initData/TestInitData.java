package com.ll.com.music_payments.app.base.initData;

import com.ll.com.music_payments.app.cart.service.CartService;
import com.ll.com.music_payments.app.member.service.MemberService;
import com.ll.com.music_payments.app.order.service.OrderService;
import com.ll.com.music_payments.app.product.service.ProductService;
import com.ll.com.music_payments.app.song.service.SongService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestInitData implements InitDataBefore{

    @Bean
    CommandLineRunner initDate(
            MemberService memberService,
            SongService songService,
            ProductService productService,
            CartService cartService,
            OrderService orderService
    ) {
        return args -> {
            before(memberService, songService, productService, cartService, orderService);
        };
    }
}
