package com.qa.automation.ui.pages;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

public class CartPage {

    private final ElementsCollection itemNames = $$("[data-test='inventory-item-name']");

    public void shouldBeOpened() {
        webdriver().shouldHave(urlContaining("cart.html"));
    }

    public void shouldContainOnly(String... productNames) {
        itemNames.shouldHave(exactTexts(productNames));
    }
}
