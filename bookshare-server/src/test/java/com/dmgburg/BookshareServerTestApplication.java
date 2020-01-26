package com.dmgburg;

import com.dmgburg.bookshareserver.BookshareServerApplication;
import com.dmgburg.book.mail.MailingService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

public class BookshareServerTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookshareTestServer.class, args);
    }
}

