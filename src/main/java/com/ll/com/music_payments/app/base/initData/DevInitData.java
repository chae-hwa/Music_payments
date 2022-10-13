package com.ll.com.music_payments.app.base.initData;

import com.ll.com.music_payments.app.cart.service.CartService;
import com.ll.com.music_payments.app.member.service.MemberService;
import com.ll.com.music_payments.app.product.service.ProductService;
import com.ll.com.music_payments.app.song.service.SongService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevInitData implements InitDataBefore {

    @Bean
    CommandLineRunner initDate(
            MemberService memberService,
            SongService songService,
            ProductService productService,
            CartService cartService
    ) {
        return args -> {
            before(memberService, songService, productService, cartService);
        };
    }
}
