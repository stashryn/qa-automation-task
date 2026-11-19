package com.qa.automation.ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    private final SelenideElement usernameInput = $("[data-test='username']");
    private final SelenideElement passwordInput = $("[data-test='password']");
    private final SelenideElement loginButton = $("[data-test='login-button']");
    private final SelenideElement error = $("[data-test='error']");

    public LoginPage open() {
        Selenide.open("/");
        loginButton.shouldBe(visible);
        return this;
    }

    public ProductsPage loginAs(String username, String password) {
        submit(username, password);
        return new ProductsPage();
    }

    public void loginWithInvalidCredentials(String username, String password) {
        submit(username, password);
    }

    public void shouldShowError(String expectedText) {
        error.shouldBe(visible).shouldHave(text(expectedText));
    }

    public void shouldBeDisplayed() {
        loginButton.shouldBe(visible);
    }

    private void submit(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        loginButton.click();
    }
}
