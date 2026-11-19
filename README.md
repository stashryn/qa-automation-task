# QA automation exercise

UI tests for [SauceDemo](https://www.saucedemo.com) and API tests for
[Restful Booker](https://restful-booker.herokuapp.com). Java 17, Selenide, REST Assured, JUnit 5, Maven.

## Prerequisites

To run locally you need:

- JDK 17 or newer
- Maven 3.8+
- Chrome (Selenium Manager downloads the matching chromedriver)
- internet access, both applications are public

To run in Docker you only need Docker.

## Local execution

```
mvn test                  # all tests
mvn test -Dgroups=ui      # UI tests
mvn test -Dgroups=api     # API tests
```

The UI tests run headless by default. To see the browser:

```
mvn test -Dgroups=ui -Dui.headless=false
```

A single class or method: `mvn test -Dtest=LoginTest` or `mvn test -Dtest=BookingApiTest#unknownBookingReturns404`.

Results are in `target/surefire-reports`. When a UI test fails, Selenide saves a screenshot and the
page source to `target/selenide-reports`.

## Docker execution

```
docker build -t qa-automation-tests .
docker run --rm qa-automation-tests
```

Extra arguments go to Maven, so a subset can be run with
`docker run --rm qa-automation-tests -Dgroups=api` (or `-Dgroups=ui`).

To keep the reports on the host: `docker run --rm -v "$(pwd)/target:/app/target" qa-automation-tests`

The image is Debian with OpenJDK 17, Maven and Chromium. Dependencies are downloaded and the tests are
compiled during `docker build`. The container exits with Maven's exit code, so it can be used in a CI job as is.

## Configuration

Defaults (URLs, credentials, timeout, headless) are in `src/test/resources/config.properties`.
Any of them can be overridden with `-Dkey=value` or with an environment variable, where dots become
underscores and everything is upper case (`ui.headless` -> `UI_HEADLESS`). The Dockerfile uses this to
point Selenide to the container's Chromium and to pass `--no-sandbox`.

## Project structure

```
src/test/java/com/qa/automation
  config/TestConfig.java        reads the configuration
  ui/BaseUiTest.java            Selenide and Chrome setup
  ui/pages/                     page objects: LoginPage, ProductsPage, CartPage
  ui/LoginTest.java
  ui/ProductCartTest.java
  api/client/BookingClient.java REST Assured requests
  api/model/                    Booking, BookingDates, CreatedBooking
  api/BookingApiTest.java
```

## Test approach

**Locators.** SauceDemo has `data-test` attributes on the elements I need (`login-button`,
`inventory-item-name`, `shopping-cart-link` and so on). They are meant for tests and do not change when
the styling or layout changes, so I used them for everything instead of CSS classes or XPath.

**Synchronization.** There are no sleeps. Selenide retries every condition (`shouldBe`, `shouldHave`,
`exactTexts`...) until it passes or the timeout (`ui.timeout.ms`, 8 s) runs out. This matters most
after sorting: instead of reading the list right away, the test waits until the prices on the page are equal
to the expected order. When the test has to read values first, it waits for the elements to be there
(`sizeGreaterThan(0)`). The page load strategy is `eager`, so tests do not wait for images.

**UI structure.** Tests use page objects. A page object owns its locators and offers actions
(`loginAs`, `sortBy`, `addFirstProductToCart`) and checks (`shouldBeOpened`, `pricesShouldBe`), so the tests
read like the scenario and contain no selectors. The expected order for the sorting test is calculated from
the prices the page showed before sorting, so the test does not depend on hard-coded product data. Every test
gets a new browser and logs in by itself, so the tests are independent.

**API structure.** `BookingClient` builds and sends the requests and returns the raw `Response`. The tests
make the assertions: status code with REST Assured, and the body is mapped to a record and compared with the
data that was sent (first name, last name, price, deposit, dates and additional needs together). Each test
creates its own booking with unique names and deletes it in `@AfterEach`. On a failed assertion REST Assured
prints the request and the response.

**Extra tests.** Besides the required scenarios there are two negative logins (wrong password and a locked
out user), `GET` of a booking that does not exist (404) and `DELETE` without credentials (403, and the booking
is still there afterwards).

## Assumptions and limitations

- Both systems are shared public demos. Restful Booker can be slow after it has been idle, or down, and anyone
  can change its data. If an API test fails with a timeout or a connection error, check that first.
- `POST /booking` returns 200 (not 201), and the test checks for what the API really returns.
- I did not add tests for invalid request bodies. Restful Booker answers them with 500, which looks like a
  bug in the demo and not something worth locking in with a test.
- Only Chrome is set up. Tests run one after another; they are independent, so parallel execution could be
  switched on later.
