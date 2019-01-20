package com.dmgburg.bookshareserver.repository;

import com.dmgburg.bookshareserver.domain.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Repository
public interface BooksRepository extends CrudRepository<Book, Long> {
    List<Book> findByOwner(String owner);

    default Stream<Book> findAllStream() {
        return StreamSupport.stream(findAll().spliterator(), false);
    };
}
