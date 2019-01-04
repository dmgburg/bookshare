package com.dmgburg.bookshareserver.repository;

import com.dmgburg.bookshareserver.domain.User;
import com.dmgburg.bookshareserver.domain.UserInteraction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInteractionRepository extends CrudRepository<UserInteraction, Long> {
}
