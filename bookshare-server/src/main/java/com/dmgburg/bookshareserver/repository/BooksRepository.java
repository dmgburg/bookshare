package com.dmgburg.bookshareserver.repository;

import com.dmgburg.bookshareserver.domain.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BooksRepository extends CrudRepository<Book, Long> {
    List<Book> findByOwner(String owner);

}
