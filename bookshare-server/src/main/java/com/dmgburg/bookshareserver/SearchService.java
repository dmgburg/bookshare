package com.dmgburg.bookshareserver;

import com.dmgburg.bookshareserver.domain.Book;
import com.dmgburg.bookshareserver.repository.BooksRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
public class SearchService {

    private final BooksRepository booksRepository;

    public SearchService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    @PostMapping("/author")
    public List<Book> searchByAuthor(@RequestParam("searchString") String searchString) {
        return booksRepository.findAllStream()
                .filter(it -> it.getAuthor().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }
}
