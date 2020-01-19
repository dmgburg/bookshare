package com.dmgburg.bookshareserver

import com.dmgburg.bookshareserver.domain.Book
import com.dmgburg.bookshareserver.domain.Cover
import com.dmgburg.bookshareserver.domain.Notification
import com.dmgburg.bookshareserver.repository.BooksRepository
import com.dmgburg.bookshareserver.repository.CoverRepository
import com.dmgburg.bookshareserver.repository.NotificationRepository
import com.google.common.base.Preconditions
import com.google.common.collect.Lists
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.security.Principal
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport

@RestController
@RequestMapping("/api/book")
class BooksService(private val booksRepository: BooksRepository,
                   private val coverRepository: CoverRepository,
                   private val coverStorage: CoverStorage,
                   private val notificationRepository: NotificationRepository,
                   private val mailingService: MailingService) {
    val log = LoggerFactory.getLogger(BooksService::class.java)

    init {
        val allCovers = coverRepository.findAll()
        val nonMigrated = allCovers.filter { it.data != null && it.data.isNotEmpty()}
        nonMigrated.forEach{
            val filename = coverStorage.saveCover(it)
            it.filename = filename
            it.data = null
            coverRepository.save(it)
            log.info("Migrated cover ${it.id} to ${it.filename}")
        }
    }

    @get:GetMapping("/public/allBooks")
    val allBooks: List<Book>
        get() = Lists.newArrayList(booksRepository.findAll())

    @GetMapping("/public/getBook/{id}")
    fun getBook(@PathVariable("id") id: Long): ResponseEntity<Book> {
        return booksRepository.findById(id)
                .map { book: Book -> ResponseEntity.ok().cacheControl(CacheControl.noCache()).body(book) }
                .orElseGet { ResponseEntity.notFound().build() }
    }

    @GetMapping(value = ["/public/getCover/{id}"])
    fun getCover(@PathVariable("id") coverId: Long): ResponseEntity<ByteArray> {
        return coverRepository.findById(coverId)
                .map { cover: Cover ->
                    val headers = HttpHeaders()
                    headers.contentType = MediaType.valueOf(cover.mediaType)
                    headers.setCacheControl(CacheControl.maxAge(60, TimeUnit.DAYS))
                    ResponseEntity(coverStorage.loadCover(cover.filename), headers, HttpStatus.OK)
                }
                .orElseGet { ResponseEntity.notFound().build() }
    }

    @GetMapping("/myBooks")
    fun getMyBooks(principal: Principal): List<Book> {
        val currentUser = principal.name
        val byOwner = booksRepository.findByOwner(currentUser).stream()
        val booksWithNotifications = StreamSupport.stream(
                booksRepository.findAllById(notificationRepository
                        .findByToUser(principal.name)
                        .stream()
                        .map { obj: Notification -> obj.book }
                        .collect(Collectors.toList()))
                        .spliterator(), false)
        return Stream.concat(byOwner, booksWithNotifications)
                .distinct()
                .collect(Collectors.toList())
    }

    @GetMapping("/addToQueue/{id}")
    @Throws(IOException::class)
    fun askForBook(@PathVariable("id") id: Long, principal: Principal): ResponseEntity<Long> {
        val optionalBook = booksRepository.findById(id)
        if (!optionalBook.isPresent) {
            return ResponseEntity.notFound().build()
        }
        val book = optionalBook.get()
        val userQueue = book.userQueue
        val currentNotification = book.notification
        if (book.owner == principal.name) {
            val notification = Notification()
                    .setToUser(principal.name)
                    .setFromUser(book.holder)
                    .setBook(book.id)
                    .setType(Notification.Type.OWNER_WANTS_THE_BOOK)
            book.notification = notification
            notificationRepository.save(notification)
        } else {
            val notification = Notification()
                    .setToUser(book.holder)
                    .setFromUser(principal.name)
                    .setBook(book.id)
                    .setType(Notification.Type.QUEUE_NOT_EMPTY)
            book.notification = notification
            notificationRepository.save(notification)
            userQueue.add(principal.name)
        }
        booksRepository.save(book)
        if (currentNotification != null) {
            notificationRepository.delete(currentNotification)
        }
        return ResponseEntity.ok(0L)
    }

    @PostMapping("/confirmHandover/{id}")
    @Throws(IOException::class)
    fun confirmHandover(@PathVariable("id") id: Long, principal: Principal): ResponseEntity<String> {
        val optionalBook = booksRepository.findById(id)
        if (!optionalBook.isPresent) {
            return ResponseEntity.notFound().build()
        }
        var book = optionalBook.get()
        val notification = book.notification
        if (notification.toUser != principal.name) {
            return ResponseEntity.badRequest().body("Книга предназанчалась кому-то другому. Попросите админа сайта разобраться")
        }
        book = book.setNotification(null).setHolder(principal.name)
        val userQueue = book.userQueue
        if (notification.type != Notification.Type.OWNER_WANTS_THE_BOOK) {
            userQueue.removeAt(0)
        }
        booksRepository.save(book)
        notificationRepository.delete(notification)
        return ResponseEntity.ok("")
    }

    @PostMapping("/handoverBook/{id}")
    @Throws(IOException::class)
    fun handoverBook(@PathVariable("id") id: Long, principal: Principal): ResponseEntity<Long> {
        val optionalBook = booksRepository.findById(id)
        if (!optionalBook.isPresent) {
            return ResponseEntity.notFound().build()
        }
        val book = optionalBook.get()
        Preconditions.checkArgument(book.holder == principal.name)
        val userQueue = book.userQueue
        val notification = Notification()
                .setFromUser(principal.name)
                .setBook(book.id)
                .setType(Notification.Type.BOOK_IS_WAITING)
                .setToUser(if (userQueue.isEmpty()) book.owner else book.userQueue[0])
        book.notification = notification
        notificationRepository.save(notification)
        booksRepository.save(book)
        return ResponseEntity.ok(0L)
    }

    @PostMapping("/removeFromQueue/{id}")
    @Throws(IOException::class)
    fun removeFromQueue(@PathVariable("id") id: Long, principal: Principal): ResponseEntity<Long> {
        val optionalBook = booksRepository.findById(id)
        if (!optionalBook.isPresent) {
            return ResponseEntity.notFound().build()
        }
        val book = optionalBook.get()
        book.userQueue.remove(principal.name)
        if (book.userQueue.isEmpty()) {
            book.notification = null
        } else {
            val notification = Notification()
                    .setFromUser(book.userQueue[0])
                    .setToUser(book.holder)
                    .setBook(book.id)
                    .setType(Notification.Type.QUEUE_NOT_EMPTY)
            notificationRepository.findByToUser(principal.name)
                    .stream()
                    .filter { it: Notification -> it.book == book.id }
                    .forEach { entity: Notification -> notificationRepository.delete(entity) }
            notificationRepository.save(notification)
            book.notification = notification
        }
        booksRepository.save(book)
        return ResponseEntity.ok(0L)
    }

    @GetMapping("/notifications")
    fun getNotifications(principal: Principal?): List<Notification> {
        return emptyList()
    }

    @PostMapping("/addBook")
    @ResponseBody
    fun addBook(@RequestBody book: Book, principal: Principal): Long {
        val name = principal.name
        book.owner = name
        book.holder = name
        return booksRepository.save(book).id
    }

    @PostMapping(value = ["/uploadCover"], consumes = ["multipart/form-data"])
    @ResponseBody
    @Throws(IOException::class)
    fun addCover(@RequestParam("data") data: MultipartFile): Long {
        val cover = Cover()
        cover.data = data.bytes
        cover.mediaType = data.contentType
        val filename = coverStorage.saveCover(cover)
        cover.filename = filename
        val save = coverRepository.save(cover)
        return save.id
    }

}