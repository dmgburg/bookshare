package com.dmgburg.bookshareserver;

import com.dmgburg.bookshareserver.domain.Book;
import com.dmgburg.bookshareserver.repository.BooksRepository;
import com.dmgburg.bookshareserver.repository.CoverRepository;
import com.dmgburg.bookshareserver.repository.NotificationRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.security.Principal;
import java.util.Optional;

@RestController
public class AdminService {

    private final JdbcTemplate jdbcTemplate;
    private final BooksRepository booksRepository;
    private final NotificationRepository notificationRepository;

    public AdminService(DataSource dataSource,
                        BooksRepository booksRepository,
                        NotificationRepository notificationRepository,
                        MailingService mailingService) {
        this.booksRepository = booksRepository;
        this.notificationRepository = notificationRepository;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @GetMapping("/api/admin/purgeNotifications")
    public String purgeNotifications(Principal principal) {
        if (principal == null || !principal.getName().equals("dmgburg@gmail.com")) {
            return "wrong user";
        }
        notificationRepository.findAll()
                .forEach(it -> {
                    Optional<Book> book = booksRepository.findById(it.getBook());
                    if (!book.isPresent()){
                        notificationRepository.delete(it);
                    } else if(!book.get().getNotification().equals(it)){
                        notificationRepository.delete(it);
                    }
                });
        return null;
    }

    @PostMapping("/api/admin/do")
    public String admin(@RequestParam String request, Principal principal) {
        if (principal == null || !principal.getName().equals("dmgburg@gmail.com")) {
            return "wrong user";
        }
        try {
            jdbcTemplate.execute(request);
            return "success";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
