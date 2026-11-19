package com.qa.automation.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.qa.automation.config.TestConfig;
import com.qa.automation.ui.pages.LoginPage;
import com.qa.automation.ui.pages.ProductsPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Arrays;
import java.util.Map;

@Tag("ui")
abstract class BaseUiTest {

    @BeforeAll
    static void setUpSelenide() {
        Configuration.baseUrl = TestConfig.get("ui.base.url");
        Configuration.browser = "chrome";
        Configuration.headless = TestConfig.headless();
        Configuration.timeout = TestConfig.uiTimeoutMs();
        Configuration.browserSize = "1920x1080";
        Configuration.pageLoadStrategy = "eager";
        Configuration.reportsFolder = "target/selenide-reports";

        String binary = TestConfig.get("browser.binary");
        if (!binary.isEmpty()) {
            Configuration.browserBinary = binary;
        }
        String driver = TestConfig.get("chromedriver.path");
        if (!driver.isEmpty()) {
            System.setProperty("webdriver.chrome.driver", driver);
        }

        ChromeOptions options = new ChromeOptions();
        String args = TestConfig.get("chrome.args");
        if (!args.isEmpty()) {
            options.addArguments(Arrays.asList(args.split(",")));
        }
        // stops Chrome from showing the "change your password" popup after login
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        Configuration.browserCapabilities = options;
    }

    // new browser for every test, so nothing (cookies, cart) leaks between tests
    @AfterEach
    void closeBrowser() {
        Selenide.closeWebDriver();
    }

    protected ProductsPage loginAsStandardUser() {
        return new LoginPage()
                .open()
                .loginAs(TestConfig.get("ui.username"), TestConfig.get("ui.password"));
    }
}
