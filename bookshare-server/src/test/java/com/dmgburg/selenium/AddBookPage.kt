package com.dmgburg.selenium

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory

class AddBookPage(private val driver: WebDriver) {

    @FindBy(className = "add-book-image")
    private val image: WebElement? = null
    @FindBy(className = "add-book-name")
    private val nameBox: WebElement? = null
    @FindBy(className = "add-book-author")
    private val authorBox: WebElement? = null
    @FindBy(className = "add-book-description")
    private val descriptionBox: WebElement? = null
    @FindBy(className = "add-book-submit")
    private val submit: WebElement? = null

    init {
        PageFactory.initElements(driver, this)
    }

    fun addBook(file: String, name: String, author: String, description: String) {
        image?.sendKeys(file)
        nameBox?.sendKeys(name)
        authorBox?.sendKeys(author)
        descriptionBox?.sendKeys(description)
        submit?.click()
    }
}