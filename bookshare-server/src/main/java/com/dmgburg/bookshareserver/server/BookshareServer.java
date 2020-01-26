package com.dmgburg.bookshareserver.server;

import com.dmgburg.bookshareserver.BookshareServerContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Import(BookshareServerContext.class)
@SpringBootApplication(scanBasePackages = {"com.dmgburg.book.mail"})
@PropertySource("file:${user.home}/secrets.properties")
public class BookshareServer {
}
