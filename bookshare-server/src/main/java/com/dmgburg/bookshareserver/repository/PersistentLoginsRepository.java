package com.dmgburg.bookshareserver.repository;

import com.dmgburg.bookshareserver.domain.PersistentLogins;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersistentLoginsRepository extends CrudRepository<PersistentLogins, Long> {
}
