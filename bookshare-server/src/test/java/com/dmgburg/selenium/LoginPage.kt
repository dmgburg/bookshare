package com.dmgburg.selenium

import com.dmgburg.waitForClass
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory

class LoginPage(private val driver: WebDriver) {

    @FindBy(className = "login-input-email")
    private val emailBox: WebElement? = null
    @FindBy(className = "login-input-password")
    private val passwordBox: WebElement? = null
    @FindBy(className = "login-input-submit")
    private val submitButton: WebElement? = null

    init {
        PageFactory.initElements(driver, this)
    }

    fun login(user: String, password: String){
        emailBox?.sendKeys(user)
        passwordBox?.sendKeys(password)
        submitButton?.click()
        driver.waitForClass("header-user-name")
    }
}