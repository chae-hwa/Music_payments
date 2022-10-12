package com.ll.com.music_payments.app.base.initData;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

public class TestDevInitData implements InitDataBefore{

    @Bean
    CommandLineRunner initDate() {
        return args -> {
            before();
        };
    }
}
