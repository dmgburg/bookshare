package com.dmgburg.bookshareserver.repository;

import com.dmgburg.bookshareserver.domain.Book;
import com.dmgburg.bookshareserver.domain.User;
import com.dmgburg.bookshareserver.domain.UserInteraction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface UserInteractionRepository extends CrudRepository<UserInteraction, Long> {
    List<UserInteraction> findByFromUser(String fromUser);
    List<UserInteraction> findByToUser(String toUser);
}
