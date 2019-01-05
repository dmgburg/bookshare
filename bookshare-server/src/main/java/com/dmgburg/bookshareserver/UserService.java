package com.dmgburg.bookshareserver;

import com.dmgburg.bookshareserver.domain.User;
import com.dmgburg.bookshareserver.repository.UserRepository;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManagerFactory;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@CrossOrigin(allowCredentials = "true", origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/user")
public class UserService implements UserDetailsService {
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
        User byEmail = userRepository.findByEmail(user.getEmail());
        return byEmail.getPasswordSalt();
    }

    @PostMapping("/public/createUser")
    @ResponseBody
    public String addUser(@RequestBody User user) {
        try (Session session = hibernateFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
//            String confirmationHash = generateConfirmationHash(50);
//            user.setConfirmationPending(confirmationHash);
            session.persist(user);
            session.flush();
            transaction.commit();
//            mailingService.sendMessage("Please confirm your email: <a href=\"\">" + user.getEmail(), user.getEmail());
            return user.getEmail();
        }
    }

    @PostMapping("/updateUser")
    @ResponseBody
    public String updateUser(@RequestBody User user) {
        return userRepository.save(user).getEmail();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email + " not found");
        }
        if (!Strings.isNullOrEmpty(user.getConfirmationPending())) {
            throw new UsernameNotFoundException(email + " is pending confirmation");
        }
        return user;
    }
}
