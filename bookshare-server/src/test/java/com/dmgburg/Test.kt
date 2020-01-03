//package com.dmgburg
//
//import com.dmgburg.bookshareserver.BookshareServerApplication
//import com.dmgburg.bookshareserver.TestClient
//import com.dmgburg.bookshareserver.domain.Book
//import org.junit.BeforeClass
//import org.junit.Test
//
//class IntegrationTest {
//
//    fun init(): Pair<String, TestClient> {
//        val testClient = TestClient()
//        val user = testClient.createUser("myUser")
//        testClient.login(user.toLowerCase())
//        return user to testClient
//    }
//
//    private fun createBook(init: Pair<String, TestClient>): Book {
//        return createBook(init.second, init.first)
//    }
//
//    private fun createBook(testClient: TestClient, user: String): Book {
//        val book = Book("testName", "testDesc", "testAuth", "testCover")
//        val id = testClient.addBook(book)
//        book.setHolder(user)
//        book.setOwner(user)
//        book.setId(id)
//        return book
//    }
//
////    @Test
////    fun `create user`() {
////        init()
////    }
////
////    @Test
////    fun `create book`() {
////        val (user, testClient) = init()
////        assert(testClient.myBooks().isEmpty())
////        val book = createBook(testClient, user)
////        val books = testClient.myBooks()
////        assert(books.size == 1)
////        assert(books[0] == book)
////    }
////
////    @Test
////    fun `created book is visible from all users`() {
////        createBook(init())
////        val (_, testClient) = init()
////        val books = testClient.allBooks()
////        assert(books.size > 0)
////        val book = testClient.getBook(books[0].id)
////        assert(book == books[0])
////        val noLogin = TestClient()
////        assert(noLogin.allBooks().size > 0)
////    }
//
//    @Test
//    fun `single round of sharing`(){
//        val (owner, ownerClient) = init()
//        val bookId = createBook(ownerClient, owner).id
//        val (reader, readerClient) = init()
//        assert(ownerClient.getBook(bookId).userQueue.size == 0)
//        assert(ownerClient.getBook(bookId).holder == owner)
//
//        readerClient.addToQueue(bookId)
//        assert(ownerClient.getBook(bookId).userQueue.size == 1)
//        assert(ownerClient.getBook(bookId).userQueue[0] == reader)
//        assert(ownerClient.getBook(bookId).holder == owner)
//
//        ownerClient.handowerBook(bookId)
//        assert(ownerClient.getBook(bookId).userQueue.size == 1)
//        assert(ownerClient.getBook(bookId).userQueue[0] == reader)
//        assert(ownerClient.getBook(bookId).holder == owner)
//
//        readerClient.confirmHandover(bookId)
//        assert(ownerClient.getBook(bookId).userQueue.size == 0)
//        assert(ownerClient.getBook(bookId).holder == reader)
//    }
//}