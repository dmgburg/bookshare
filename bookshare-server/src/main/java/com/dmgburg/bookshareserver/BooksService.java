package com.dmgburg.bookshareserver;

import com.dmgburg.bookshareserver.domain.Book;
import com.dmgburg.bookshareserver.domain.Cover;
import com.dmgburg.bookshareserver.repository.BooksRepository;
import com.dmgburg.bookshareserver.repository.CoverRepository;
import com.google.common.collect.Lists;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@CrossOrigin(allowCredentials = "true", origins = "http://localhost:3000")
@RestController
public class BooksService {
    private final BooksRepository booksRepository;
    private final CoverRepository coverRepository;

    public BooksService(BooksRepository booksRepository, CoverRepository coverRepository) {
        this.booksRepository = booksRepository;
        this.coverRepository = coverRepository;
    }

    @GetMapping("/allBooks")
    public List<Book> getAllBooks() {
        return Lists.newArrayList(booksRepository.findAll());
    }

    @GetMapping("/myBooks")
    public List<Book> getMyBooks(Principal principal) {
        String currentUser = principal.getName();
        return Lists.newArrayList(booksRepository.findByOwner(currentUser));
    }

    @GetMapping("/getBook/{id}")
    public ResponseEntity<Book> getAllBooks(@PathVariable("id") Long id) {
        return ResponseEntity.of(booksRepository.findById(id));
    }

    @PostMapping("/addBook")
    @ResponseBody
    public Long addBook(@RequestBody Book book, Principal principal) {
        String name = principal.getName();
        book.setOwner(name);
        book.setOwner(name);
        return booksRepository.save(book).getId();
    }

    @PostMapping(value = "/uploadCover", consumes = "multipart/form-data")
    @ResponseBody
    public Long addCover(@RequestParam("data") MultipartFile data) throws IOException {
        Cover cover = new Cover();
        cover.setData(data.getBytes());
        cover.setMediaType(data.getContentType());
        Cover save = coverRepository.save(cover);
        return save.getId();
    }

    @GetMapping(value = "/getCover/{id}")
    public ResponseEntity<byte[]> getCover(@PathVariable("id") Long coverId) {
        Optional<Cover> optionalCover = coverRepository.findById(coverId);
        if (!optionalCover.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Cover cover = optionalCover.get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(cover.getMediaType()));
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        return new ResponseEntity<>(cover.getData(), headers, HttpStatus.OK);
    }
}