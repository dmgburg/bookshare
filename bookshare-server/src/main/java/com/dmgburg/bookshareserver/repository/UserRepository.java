package com.dmgburg.bookshareserver.repository;

import com.dmgburg.bookshareserver.domain.Book;
import com.dmgburg.bookshareserver.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
}
