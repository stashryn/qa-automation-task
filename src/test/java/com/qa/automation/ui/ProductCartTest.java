package com.qa.automation.ui;

import com.qa.automation.ui.pages.CartPage;
import com.qa.automation.ui.pages.ProductsPage;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

class ProductCartTest extends BaseUiTest {

    @Test
    void productFromSortedListCanBeAddedToCart() {
        ProductsPage productsPage = loginAsStandardUser();
        productsPage.shouldBeOpened();

        // expected order is calculated from the prices shown before sorting
        List<String> expectedPrices = productsPage.prices().stream()
                .sorted(Comparator.comparing(ProductCartTest::toNumber))
                .toList();

        productsPage.sortBy("Price (low to high)");
        productsPage.pricesShouldBe(expectedPrices);

        String productName = productsPage.addFirstProductToCart();
        productsPage.cartBadgeShouldShow(1);

        CartPage cartPage = productsPage.openCart();
        cartPage.shouldBeOpened();
        cartPage.shouldContainOnly(productName);
    }

    private static BigDecimal toNumber(String price) {
        return new BigDecimal(price.replace("$", ""));
    }
}
