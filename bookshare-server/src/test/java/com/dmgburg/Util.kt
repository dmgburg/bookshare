package com.dmgburg

import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.LoggerFactory
import java.lang.RuntimeException
import java.net.URI
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

private val charPool: List<Char> = ('A'..'Z') + ('0'..'9')
private val STRING_LENGTH = 10
private val log = LoggerFactory.getLogger("test Util")

fun randomStr() = ThreadLocalRandom.current()
        .ints(STRING_LENGTH.toLong(), 0, charPool.size)
        .asSequence()
        .map(charPool::get)
        .joinToString("")


fun WebDriver.waitForClass(classAss: String) {
    for (i in 1..5) {
        try {
            WebDriverWait(this, 1)
                    .until(ExpectedConditions.visibilityOfElementLocated(By.className(classAss)))
            return
        } catch (e: TimeoutException) {
            this.navigate().refresh()
            Thread.sleep(500)
        }
    }
    throw RuntimeException(classAss + " not found")
}

fun WebDriver.getRelative(url: String) {
    val relative = URI(getCurrentUrl()).resolve(url).toString()
    log.info("getting page '$relative'")
    this.get(relative)
}

fun WebDriver.findElementByClassName(classname: String) = this.findElement(By.className(classname))