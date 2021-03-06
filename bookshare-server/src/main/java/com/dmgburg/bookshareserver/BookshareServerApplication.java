package com.dmgburg.bookshareserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public class BookshareServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookshareServerContext.class, args);
    }
}

