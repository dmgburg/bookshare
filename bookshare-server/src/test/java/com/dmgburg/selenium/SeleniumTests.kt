package com.dmgburg.selenium

import com.dmgburg.bookshareserver.BookshareServerApplication
import com.dmgburg.findElementByClassName
import com.dmgburg.getRelative
import com.dmgburg.randomStr
import com.dmgburg.waitForClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals


class SeleniumTests {

    companion object {
        @JvmStatic
        @BeforeClass
        fun startServer() {
            System.setProperty("spring.profiles.active", "test")
            BookshareServerApplication.main(emptyArray())
        }

        val serverUrl : String? = System.getProperty("server.url") ?: "http://localhost:8080"
    }

    @Before
    fun setup() {
//        if(System.getProperty("webdriver.chrome.driver") == null) {
//            System.setProperty("webdriver.chrome.driver", "/usr/bin/google-chrome-stable")
//        }
//        if(System.getProperty("webdriver.remote.server") == null) {
//            System.setProperty("webdriver.remote.server", "http://localhost:9222/wd/hub")
//        }
    }

    private fun createDriver(): WebDriver {
        val options = ChromeOptions()
        options.setHeadless(true)
        val driver = RemoteWebDriver(URL("http://localhost:9222"), DesiredCapabilities.chrome())
        driver.manage()?.timeouts()?.implicitlyWait(10, TimeUnit.SECONDS)
        driver.get(serverUrl)
        return driver
    }

    private fun WebDriver.loginUser(user: String, password: String) {
        val guest = GuestHeader(this)
        guest.goToLogin()
        val loginPage = LoginPage(this)
        loginPage.login(user, password)
    }

    private fun WebDriver.registerUser(user: String, password: String) {
        val guest = GuestHeader(this)
        guest.goToRegister()
        val registerPage = RegisterPage(this)
        registerPage.registerUser(user, password)
    }

    private fun WebDriver.addBookAndCheck(user: String): Long {
        UserHeader(this).goToAddBook()
        val addBookPage = AddBookPage(this)
        val name = randomStr() + "addBookName"
        val author = randomStr() + "addBookAuthor"
        val description = randomStr() + "addBookDescription"
        val tempFile = createTempFile()
        try {
            val fileContent = SeleniumTests::class.java.getResource("/focus.jpg").readBytes()
            tempFile.writeBytes(fileContent)
            addBookPage.addBook(tempFile.absolutePath, name, author, description)
        } finally {
            tempFile.delete()
        }
        this.waitForClass("details-name")
        val details = BookDetailsPage(this)
        assertEquals(name, details.name)
        assertEquals(author, details.author)
        assertEquals(user, details.owner)
        assertEquals(user, details.holder)
        assertEquals(description, details.description)
        val currentUrl = this.currentUrl
        return currentUrl.substring(currentUrl.lastIndexOf("/") + 1).toLong()
    }

    private fun WebDriver.newLogin(): Pair<String, String> {
        val user = randomStr() + "registerTest"
        val password = randomStr()
        registerUser(user, password)
        loginUser(user.toLowerCase(), password)
        return Pair(user.toLowerCase(), password)
    }

    private fun <R> withAndClose(webDriver: WebDriver, block: WebDriver.() -> R): R {
        try {
            return block.invoke(webDriver)
        } finally {
            webDriver.close()
        }
    }

    @Test
    fun registerUser() {
        withAndClose(createDriver()) {
            val (user, _) = newLogin()
            val userHeader = UserHeader(this)
            assert(userHeader.username == user.toLowerCase())
        }
    }

    @Test
    fun addBookUser() {
        withAndClose(createDriver()) {
            val (user, _) = newLogin()
            addBookAndCheck(user)
        }
    }

    @Test
    fun askBookFlow() {
        val ownerDriver = createDriver()
        val borrowerDriver = createDriver()
        try {
            val (owner, _) = ownerDriver.newLogin()
            val bookId = ownerDriver.addBookAndCheck(owner)

            val (_, _) = borrowerDriver.newLogin()
            borrowerDriver.getRelative("/bookDetails/$bookId")
            borrowerDriver.waitForClass("action-ask-book")
            borrowerDriver.findElementByClassName("action-ask-book").click()

            ownerDriver.getRelative("/bookDetails/$bookId")
            ownerDriver.waitForClass("action-handover")
            ownerDriver.findElementByClassName("action-handover").click()
            ownerDriver.getRelative("/bookDetails/$bookId")

            borrowerDriver.getRelative("/bookDetails/$bookId")
            borrowerDriver.waitForClass("action-confirm-handover")
            borrowerDriver.findElementByClassName("action-confirm-handover").click()

            borrowerDriver.waitForClass("action-you-have-the-book")
        } finally {
            ownerDriver.close()
            borrowerDriver.close()
        }
    }

    @Test
    fun askNowBookQueueFlow() {
        val ownerDriver = createDriver()
        val borrowerDriver = createDriver()
        val borrower2Driver = createDriver()
        try {
            val (owner, _) = ownerDriver.newLogin()
            val bookId = ownerDriver.addBookAndCheck(owner)

            val (_, _) = borrowerDriver.newLogin()
            borrowerDriver.getRelative("/bookDetails/$bookId")
            borrowerDriver.waitForClass("action-ask-book")
            borrowerDriver.findElementByClassName("action-ask-book").click()

            val (_, _) = borrower2Driver.newLogin()
            borrower2Driver.getRelative("/bookDetails/$bookId")
            borrower2Driver.waitForClass("action-ask-book")
            borrower2Driver.findElementByClassName("action-ask-book").click()

            ownerDriver.getRelative("/bookDetails/$bookId")
            ownerDriver.waitForClass("action-handover")
            ownerDriver.findElementByClassName("action-handover").click()

            borrowerDriver.getRelative("/bookDetails/$bookId")
            borrowerDriver.waitForClass("action-confirm-handover")
            borrowerDriver.findElementByClassName("action-confirm-handover").click()

            borrowerDriver.getRelative("/bookDetails/$bookId")
            borrowerDriver.waitForClass("action-handover")

            borrower2Driver.getRelative("/bookDetails/$bookId")
            borrower2Driver.waitForClass("action-already-in-queue")

            ownerDriver.getRelative("/bookDetails/$bookId")
            ownerDriver.waitForClass("action-request-now")
            ownerDriver.findElementByClassName("action-request-now").click()

            ownerDriver.getRelative("/bookDetails/$bookId")
            ownerDriver.waitForClass("action-requested-now")
            ownerDriver.findElementByClassName("action-confirm-handover").click()

            ownerDriver.getRelative("/bookDetails/$bookId")
            ownerDriver.waitForClass("action-your-book")

            borrowerDriver.getRelative("/bookDetails/$bookId")
            borrowerDriver.waitForClass("action-ask-book")

            borrower2Driver.getRelative("/bookDetails/$bookId")
            borrower2Driver.waitForClass("action-already-in-queue")
        } finally {
            ownerDriver.close()
            borrowerDriver.close()
            borrower2Driver.close()
        }
    }
}