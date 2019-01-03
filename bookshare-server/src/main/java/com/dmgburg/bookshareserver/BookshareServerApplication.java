package com.dmgburg.bookshareserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@SpringBootApplication(scanBasePackages = {"com.dmgburg.book.security", "com.dmgburg.bookshareserver"})
public class BookshareServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookshareServerApplication.class, args);
    }

    @Bean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter();
    }
}

