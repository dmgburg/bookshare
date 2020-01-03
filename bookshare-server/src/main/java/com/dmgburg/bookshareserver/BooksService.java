package com.dmgburg.bookshareserver;

import com.dmgburg.bookshareserver.domain.Book;
import com.dmgburg.bookshareserver.domain.Cover;
import com.dmgburg.bookshareserver.domain.Notification;
import com.dmgburg.bookshareserver.repository.BooksRepository;
import com.dmgburg.bookshareserver.repository.CoverRepository;
import com.dmgburg.bookshareserver.repository.NotificationRepository;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/book")
public class BooksService {
    private final BooksRepository booksRepository;
    private final CoverRepository coverRepository;
    private final NotificationRepository notificationRepository;
    private final MailingService mailingService;

    public BooksService(BooksRepository booksRepository,
                        CoverRepository coverRepository,
                        NotificationRepository notificationRepository,
                        MailingService mailingService) {
        this.booksRepository = booksRepository;
        this.coverRepository = coverRepository;
        this.notificationRepository = notificationRepository;
        this.mailingService = mailingService;
    }

    @GetMapping("/public/allBooks")
    public List<Book> getAllBooks() {
        return Lists.newArrayList(booksRepository.findAll());
    }

    @GetMapping("/public/getBook/{id}")
    public ResponseEntity<Book> getBook(@PathVariable("id") Long id) {
        return ResponseEntity.of(booksRepository.findById(id));
    }

    @GetMapping(value = "/public/getCover/{id}")
    public ResponseEntity<byte[]> getCover(@PathVariable("id") Long coverId) {
        Optional<Cover> optionalCover = coverRepository.findById(coverId);
        if (!optionalCover.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Cover cover = optionalCover.get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(cover.getMediaType()));
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        return new ResponseEntity<>(cover.getData(), headers, HttpStatus.OK);
    }

    @GetMapping("/myBooks")
    public List<Book> getMyBooks(Principal principal) {
        String currentUser = principal.getName();
        Stream<Book> byOwner = booksRepository.findByOwner(currentUser).stream();
        Stream<Book> booksWithNotifications = StreamSupport.stream(
                booksRepository.findAllById(notificationRepository
                        .findByToUser(principal.getName())
                        .stream()
                        .map(Notification::getBook)
                        .collect(Collectors.toList()))
                        .spliterator(), false);
        return Stream.concat(byOwner, booksWithNotifications)
                .distinct()
                .collect(Collectors.toList());
    }

    @GetMapping("/addToQueue/{id}")
    public ResponseEntity<Long> askForBook(@PathVariable("id") Long id, Principal principal) throws IOException {
        Optional<Book> optionalBook = booksRepository.findById(id);
        if (!optionalBook.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Book book = optionalBook.get();
        List<String> userQueue = book.getUserQueue();
        Notification currentNotification = book.getNotification();
        if (book.getOwner().equals(principal.getName())){
            Notification notification = new Notification()
                    .setToUser(principal.getName())
                    .setFromUser(book.getHolder())
                    .setBook(book.getId())
                    .setType(Notification.Type.OWNER_WANTS_THE_BOOK);
            book.setNotification(notification);
            notificationRepository.save(notification);
        } else {
            Notification notification = new Notification()
                    .setToUser(book.getHolder())
                    .setFromUser(principal.getName())
                    .setBook(book.getId())
                    .setType(Notification.Type.QUEUE_NOT_EMPTY);
            book.setNotification(notification);
            notificationRepository.save(notification);
            userQueue.add(principal.getName());
        }
        booksRepository.save(book);
        if(currentNotification != null){
            notificationRepository.delete(currentNotification);
        }
        return ResponseEntity.ok(0L);
    }

    @PostMapping("/confirmHandover/{id}")
    public ResponseEntity<String> confirmHandover(@PathVariable("id") Long id, Principal principal) throws IOException {
        Optional<Book> optionalBook = booksRepository.findById(id);
        if (!optionalBook.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Book book = optionalBook.get();

        Notification notification = book.getNotification();
        if (!notification.getToUser().equals(principal.getName())){
            return ResponseEntity.badRequest().body("Книга предназанчалась кому-то другому. Попросите админа сайта разобраться");
        }
        book = book.setNotification(null).setHolder(principal.getName());
        List<String> userQueue = book.getUserQueue();
        if(notification.getType() != Notification.Type.OWNER_WANTS_THE_BOOK) {
            userQueue.remove(0);
        }
        booksRepository.save(book);
        notificationRepository.delete(notification);
        return ResponseEntity.ok("");
    }

    @PostMapping("/handoverBook/{id}")
    public ResponseEntity<Long> handoverBook(@PathVariable("id") Long id, Principal principal) throws IOException {
        Optional<Book> optionalBook = booksRepository.findById(id);
        if (!optionalBook.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Book book = optionalBook.get();

        Preconditions.checkArgument(book.getHolder().equals(principal.getName()));

        List<String> userQueue = book.getUserQueue();
        Notification notification = new Notification()
                .setFromUser(principal.getName())
                .setBook(book.getId())
                .setType(Notification.Type.BOOK_IS_WAITING)
                .setToUser(userQueue.isEmpty() ? book.getOwner() : book.getUserQueue().get(0));
        book.setNotification(notification);
        notificationRepository.save(notification);
        booksRepository.save(book);
        return ResponseEntity.ok(0L);
    }

    @GetMapping("/removeFromQueue/{id}")
    public ResponseEntity<Long> removeFromQueue(@PathVariable("id") Long id, Principal principal) throws IOException {
        Optional<Book> optionalBook = booksRepository.findById(id);
        if (!optionalBook.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Book book = optionalBook.get();
        book.getUserQueue().remove(principal.getName());
        if(book.getUserQueue().isEmpty()){
            book.setNotification(null);
        } else {
            Notification notification = new Notification()
                    .setFromUser(book.getUserQueue().get(0))
                    .setToUser(book.getHolder())
                    .setBook(book.getId())
                    .setType(Notification.Type.QUEUE_NOT_EMPTY);
            notificationRepository.findByToUser(principal.getName())
                    .stream()
                    .filter(it -> it.getBook() == book.getId())
                    .forEach(notificationRepository::delete);
            notificationRepository.save(notification);
            book.setNotification(notification);
        }
        booksRepository.save(book);
        return ResponseEntity.ok(0L);
    }

    @GetMapping("/notifications")
    public List<Notification> getNotifications(Principal principal) {
        return Collections.emptyList();
    }

    @PostMapping("/addBook")
    @ResponseBody
    public Long addBook(@RequestBody Book book, Principal principal) {
        String name = principal.getName();
        book.setOwner(name);
        book.setHolder(name);
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
}