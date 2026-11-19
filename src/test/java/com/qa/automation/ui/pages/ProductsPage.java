package com.qa.automation.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

public class ProductsPage {

    private final SelenideElement title = $("[data-test='title']");
    private final SelenideElement sortSelect = $("[data-test='product-sort-container']");
    private final SelenideElement cartLink = $("[data-test='shopping-cart-link']");
    private final SelenideElement cartBadge = $("[data-test='shopping-cart-badge']");
    private final ElementsCollection products = $$("[data-test='inventory-item']");
    private final ElementsCollection productNames = $$("[data-test='inventory-item-name']");
    private final ElementsCollection productPrices = $$("[data-test='inventory-item-price']");

    public void shouldBeOpened() {
        webdriver().shouldHave(urlContaining("inventory.html"));
        title.shouldHave(exactText("Products"));
    }

    public void shouldContainProducts() {
        products.shouldHave(sizeGreaterThan(0));
    }

    /** Prices as shown on the page, in the current order, e.g. "$29.99". */
    public List<String> prices() {
        return productPrices.shouldHave(sizeGreaterThan(0)).stream()
                .map(SelenideElement::text)
                .toList();
    }

    public void sortBy(String optionText) {
        sortSelect.selectOption(optionText);
    }

    public void pricesShouldBe(List<String> expected) {
        productPrices.shouldHave(exactTexts(expected));
    }

    /** Adds the first product on the page to the cart and returns its name. */
    public String addFirstProductToCart() {
        String name = productNames.first().text();
        products.first().$("button").click();
        return name;
    }

    public void cartBadgeShouldShow(int count) {
        cartBadge.shouldHave(exactText(String.valueOf(count)));
    }

    public CartPage openCart() {
        cartLink.click();
        return new CartPage();
    }
}
