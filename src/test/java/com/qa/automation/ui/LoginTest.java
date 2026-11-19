package com.qa.automation.ui;

import com.qa.automation.config.TestConfig;
import com.qa.automation.ui.pages.LoginPage;
import com.qa.automation.ui.pages.ProductsPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LoginTest extends BaseUiTest {

    @Test
    void validUserReachesProductsPage() {
        ProductsPage productsPage = new LoginPage()
                .open()
                .loginAs(TestConfig.get("ui.username"), TestConfig.get("ui.password"));

        productsPage.shouldBeOpened();
        productsPage.shouldContainProducts();
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "standard_user | wrong_password | Username and password do not match",
            "locked_out_user | secret_sauce | this user has been locked out"
    })
    void invalidLoginShowsError(String username, String password, String expectedError) {
        LoginPage loginPage = new LoginPage().open();

        loginPage.loginWithInvalidCredentials(username, password);

        loginPage.shouldShowError(expectedError);
        loginPage.shouldBeDisplayed();
    }
}
