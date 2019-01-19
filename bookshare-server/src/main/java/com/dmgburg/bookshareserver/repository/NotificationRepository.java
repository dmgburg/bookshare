package com.dmgburg.bookshareserver.repository;

import com.dmgburg.bookshareserver.domain.Book;
import com.dmgburg.bookshareserver.domain.Notification;
import com.dmgburg.bookshareserver.domain.User;
import com.dmgburg.bookshareserver.domain.UserInteraction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
    List<Notification> findByFromUser(String fromUser);
    List<Notification> findByToUser(String toUser);
}
