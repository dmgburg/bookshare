package com.dmgburg.bookshareserver;

import com.dmgburg.bookshareserver.domain.User;
import com.dmgburg.bookshareserver.repository.UserRepository;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManagerFactory;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@CrossOrigin(allowCredentials = "true", origins = "http://localhost:3000")
@RestController
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final SessionFactory hibernateFactory;

    public UserService(UserRepository userRepository, EntityManagerFactory factory) {
        this.userRepository = userRepository;
        if (factory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("factory is not a hibernate factory");
        }
        this.hibernateFactory = factory.unwrap(SessionFactory.class);
    }



    @GetMapping("/currentUser")
    @ResponseBody
    public String getCurrentUser(Principal principal) {
        return Optional.ofNullable(principal).map(Principal::getName).orElse("");
    }

    @GetMapping("/allUsers")
    @ResponseBody
    public List<String> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .filter(Objects::nonNull)
                .map(User::getEmail)
                .collect(Collectors.toList());
    }

    @PostMapping("/createUser")
    @ResponseBody
    public String addUser(@RequestBody User user) {
        try (Session session = hibernateFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            session.flush();
            transaction.commit();
            return user.getEmail();
        }
    }

    @PostMapping("/updateUser")
    @ResponseBody
    public String updateUser(@RequestBody User user) {
        return userRepository.save(user).getEmail();
    }

    @PostMapping("/userSalt")
    @ResponseBody
    public String userSalt(@RequestBody User user){
        User byEmail = userRepository.findByEmail(user.getEmail());
        return byEmail.getPasswordSalt();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email + " not found");
        }

        return user;
    }
}
