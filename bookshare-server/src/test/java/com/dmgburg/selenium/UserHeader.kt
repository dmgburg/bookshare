package com.dmgburg.selenium

import com.dmgburg.waitForClass
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory

class UserHeader(private val driver: WebDriver){

    @FindBy(className = "header-user-name")
    private val nameLabel: WebElement? = null

    @FindBy(className = "header-user-logout")
    private val logoutLink: WebElement? = null

    @FindBy(className = "header-user-books-add")
    private val addBookLink: WebElement? = null

    val username: String? get() = nameLabel?.text

    init {
        PageFactory.initElements(driver, this)
    }

    fun goToAddBook(){
        addBookLink?.click()
    }
}