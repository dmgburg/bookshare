package com.dmgburg;

import com.dmgburg.book.mail.MailingService;
import com.dmgburg.bookshareserver.BookshareServerContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Import(BookshareServerContext.class)
public class BookshareTestServer {
    @Bean
    public MailingService mailingService(){
        return new TestMailingService();
    }
}
