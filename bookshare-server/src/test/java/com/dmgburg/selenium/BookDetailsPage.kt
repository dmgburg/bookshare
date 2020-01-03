package com.dmgburg.selenium

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory

class BookDetailsPage(private val driver: WebDriver) {

    @FindBy(className = "details-name")
    private val nameLabel: WebElement? = null
    @FindBy(className = "details-author")
    private val authorLabel: WebElement? = null
    @FindBy(className = "details-owner")
    private val ownerLabel: WebElement? = null
    @FindBy(className = "details-holder")
    private val holderLabel: WebElement? = null
    @FindBy(className = "details-description")
    private val descriptionLabel: WebElement? = null

    val name: String? get() = nameLabel?.text
    val author: String? get() = authorLabel?.text
    val owner: String? get() = ownerLabel?.text
    val holder: String? get() = holderLabel?.text
    val description: String? get() = descriptionLabel?.text

    init {
        PageFactory.initElements(driver, this)
    }
}