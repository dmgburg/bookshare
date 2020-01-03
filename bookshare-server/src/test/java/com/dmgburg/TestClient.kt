package com.dmgburg.bookshareserver

import com.dmgburg.bookshareserver.domain.Book
import com.dmgburg.bookshareserver.domain.User
import com.dmgburg.randomStr
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.cookies.AcceptAllCookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.cookies.cookies
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

class TestClient(val host: String = "localhost", val port: Int = 8080) {

    val client = HttpClient(Apache) {
        engine {
            socketTimeout = 100_000
        }
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
        install(HttpCookies) {
            // Will keep an in-memory map with all the cookies from previous requests.
            storage = AcceptAllCookiesStorage()
        }
    }

    fun printCoockies() {
        runBlocking {
            client.cookies("$host:$port")
        }
    }

    fun HttpRequestBuilder.path(path: String) = url("http://$host:$port/$path")

    fun createUser(userEmail: String) = runBlocking {
        client.post<String> {
            path("api/user/public/createUser")
            contentType(ContentType.Application.Json)
            body = User().apply {
                email = randomStr() + userEmail.capitalize()
                passwordHash = "000"
            }
        }
    }

    fun login(userEmail: String) = runBlocking {
        client.submitForm<String>(formParameters = Parameters.build {
            append("username", userEmail)
            append("password", "000")
        }) {
            path("login")
        }
    }

    fun myBooks() = runBlocking {
        client.get<List<Book>> {
            path("api/book/myBooks")
        }
    }

    fun allBooks() = runBlocking {
        client.get<List<Book>> {
            path("api/book/public/allBooks")
        }
    }

    fun getBook(id: Long) = runBlocking {
        client.get<Book> {
            path("api/book/public/getBook/$id")
        }
    }

    fun addBook(book: Book) = runBlocking {
        client.post<Long> {
            path("api/book/addBook")
            contentType(ContentType.Application.Json)
            body = book
        }
    }

    fun addToQueue(bookId: Long) = runBlocking {
        client.get<Unit> {
            path("api/book/addToQueue/$bookId")
        }
    }

    fun handowerBook(bookId: Long) = runBlocking {
        client.post<Unit> {
            path("api/book/handoverBook/$bookId")
        }
    }

    fun confirmHandover(bookId: Long) = runBlocking {
        client.post<Unit> {
            path("api/book/confirmHandover/$bookId")
        }
    }
}