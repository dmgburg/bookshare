package com.dmgburg.bookshareserver.repository;

import com.dmgburg.bookshareserver.domain.Book;
import com.dmgburg.bookshareserver.domain.Cover;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverRepository extends CrudRepository<Cover, Long> {}
