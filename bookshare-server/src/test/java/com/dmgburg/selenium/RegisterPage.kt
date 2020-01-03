package com.dmgburg.selenium

import com.dmgburg.waitForClass
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory

class RegisterPage(private val driver: WebDriver) {

    @FindBy(className = "register-input-email")
    private val emailBox: WebElement? = null
    @FindBy(className = "register-input-password")
    private val passwordBox: WebElement? = null
    @FindBy(className = "register-input-password2")
    private val password2Box: WebElement? = null
    @FindBy(className = "register-input-submit")
    private val submitButton: WebElement? = null

    init {
        PageFactory.initElements(driver, this)
    }

    fun registerUser(email: String, password: String) {
        emailBox?.sendKeys(email)
        passwordBox?.sendKeys(password)
        password2Box?.sendKeys(password)
        submitButton?.click()
        driver.waitForClass("login-input-email")
    }
}