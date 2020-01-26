package com.dmgburg.bookshareserver;

import com.dmgburg.book.mail.MailingService;
import com.dmgburg.bookshareserver.domain.User;
import com.dmgburg.bookshareserver.repository.UserRepository;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriUtils;

import javax.persistence.EntityManagerFactory;
import java.security.Principal;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/user")
public class UserService implements UserDetailsService {
    private final static Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final SessionFactory hibernateFactory;
    private final MailingService mailingService;
    private final Random random = new Random(); // or SecureRandom

    public UserService(UserRepository userRepository, EntityManagerFactory factory, MailingService mailingService) {
        this.userRepository = userRepository;
        if (factory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("factory is not a hibernate factory");
        }
        this.hibernateFactory = factory.unwrap(SessionFactory.class);
        this.mailingService = mailingService;
    }

    public String generateConfirmationHash(int length) {
        final byte[] buffer = new byte[length];
        random.nextBytes(buffer);
        String encode = BaseEncoding.base64Url().omitPadding().encode(buffer);

        return encode;
    }


    @GetMapping("/public/currentUser")
    @ResponseBody
    public String getCurrentUser(Principal principal) {
        return Optional.ofNullable(principal).map(Principal::getName).orElse("");
    }

    @PostMapping("/public/userSalt")
    @ResponseBody
    public String userSalt(@RequestBody User user) {
        User byEmail = userRepository.findByEmail(user.getEmail().toLowerCase());
        if (byEmail == null) {
            throw new IllegalArgumentException("Пользователь " + user.getEmail() + " не зарегистрирован");
        }
        return byEmail.getPasswordSalt();
    }

    @PostMapping("/public/createUser")
    @ResponseBody
    public String addUser(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail().toLowerCase()) != null) {
            throw new IllegalArgumentException("Пользователь " + user.getEmail() + " уже зарегистрирован");
        }
        try (Session session = hibernateFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String confirmationHash = generateConfirmationHash(50);
            user.setConfirmationPending(confirmationHash);
            user.setEmail(user.getEmail().toLowerCase());
            mailingService.sendEmailConfirmation(user.getEmail(), confirmationHash);
            session.persist(user);
            session.flush();
            transaction.commit();
            return user.getEmail();
        }
    }

    @GetMapping("/public/confirmUser/{email}/{hash}")
    public View confirmUser(@PathVariable("email") String email, @PathVariable("hash") String hash) {
        String decodedEmail = UriUtils.decode(email, "UTF-8").toLowerCase();
        User user = userRepository.findByEmail(decodedEmail);
        if (user != null && hash.equals(user.getConfirmationPending())) {
            log.info("User '{}' successfully confirmed email", decodedEmail);
            user.setConfirmationPending(null);
            userRepository.save(user);
            return new RedirectView("/signin");
        } else {
            log.info("User '{}' failed to confirm email", decodedEmail);
            return new RedirectView("/");
        }
    }

    @PostMapping("/updateUser")
    @ResponseBody
    public String updateUser(@RequestBody User user) {
        return userRepository.save(user).getEmail();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email.toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException(email + " not found");
        }
        if (!Strings.isNullOrEmpty(user.getConfirmationPending())) {
            throw new IllegalStateException(email + " is pending confirmation");
        }
        return user;
    }
}
