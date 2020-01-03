package com.dmgburg.selenium

import com.dmgburg.waitForClass
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory

class GuestHeader(private val driver: WebDriver) {
    @FindBy(className = "header-guest-register")
    private val registerLink: WebElement? = null

    @FindBy(className = "header-guest-login")
    private val loginLink: WebElement? = null

    init {
        driver.waitForClass("header-guest-register")
        PageFactory.initElements(driver, this)
    }

    fun goToRegister(){
        registerLink?.click()
    }

    fun goToLogin(){
        loginLink?.click()
    }
}