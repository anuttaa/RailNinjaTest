package org.example;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.example.passengerTests.MainSearchPage;
import org.example.passengerTests.PassengerPage;
import org.example.passengerTests.TabUtils;
import org.example.passengerTests.TrainResultsPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class FormTest {

    MainSearchPage mainPage;
    TrainResultsPage resultsPage;
    PassengerPage passengerPage;

    public static Stream<Arguments> passengerDataProvider() {
        return Stream.of(
                Arguments.of("johndoe@gmail.com", "John Doe", "Male", "Belarus", "AB1234567", 15, "December", 2000),
                Arguments.of("another@example.com", "Jane Smith", "Female", "Ukraine", "UK7654321", 25, "July", 1990),
                Arguments.of("testuser@mail.com", "Robert Johnson", "Male", "Poland", "PL9876543", 10, "March", 2005),
                Arguments.of("user123@test.org", "Maria Garcia", "Female", "Spain", "ES4567890", 5, "December", 1995)
        );
    }

    @BeforeEach
    public void config(){
        Configuration.holdBrowserOpen = true;
        Configuration.timeout = 20000;
        Configuration.browserSize = "1920x1080";

        if (WebDriverRunner.hasWebDriverStarted()) {
            WebDriverRunner.closeWebDriver();
        }

        mainPage = new MainSearchPage();
        resultsPage = new TrainResultsPage();
        passengerPage = new PassengerPage();

        open("https://rail.ninja/");
        $("body").shouldBe(visible, Duration.ofSeconds(15));

        mainPage.setDepartureStation("Mecca")
                .setArrivalStation("Medina")
                .setDate("2025-11-05")
                .search();

        TabUtils.waitForNewTab(20);
        TabUtils.switchToNewTabAndCloseOld();

        $("body").shouldBe(visible, Duration.ofSeconds(20));
        resultsPage.selectSeats();
        resultsPage.bookFlexibleTicket();
    }

    @ParameterizedTest
    @MethodSource("org.example.FormTest#passengerDataProvider")
    public void testPassengerFormWithDifferentData(
            String email,
            String name,
            String gender,
            String citizenship,
            String passportNumber,
            int day, String month, int year) {

        passengerPage.setEmail(email);
        passengerPage.setAdultPassengerName(name);
        passengerPage.selectGender(gender);
        passengerPage.setCitizenship(citizenship);
        passengerPage.setPassportNumber(passportNumber);
        passengerPage.setBirthDate(day, month, year);

        $$("button").findBy(text("Continue")).click();
        $("#payment-methods-fields").shouldBe(visible, Duration.ofSeconds(10));
        Assertions.assertTrue($("#payment-methods-fields").isDisplayed(),
                "Не удалось перейти на страницу оплаты для данных: " + email);

        System.out.println("Успешно завершен тест для: " + name + " (" + email + ")");
    }

    @Test
    public void testPassengerFormWithEmptyRequiredFields() {

        passengerPage.setEmail("test@test.com");
        passengerPage.setAdultPassengerName("Test User");

        $$("button").findBy(text("Continue")).click();

        $(".ant-form-item-explain-error").shouldBe(visible, Duration.ofSeconds(5));

        Assertions.assertFalse($("#payment-methods-fields").exists(),
                "Неожиданно перешли на страницу оплаты при незаполненных обязательных полях");

        String pageText = $("body").getText();
        Assertions.assertTrue(pageText.contains("required") ||
                        pageText.contains("Required") ||
                        $(".ant-form-item-explain-error").exists(),
                "Не найдены сообщения об ошибках для обязательных полей");
    }

    @Test
    public void testPassengerFormWithInvalidData() {

        passengerPage.setEmail("invalid-email");
        passengerPage.setAdultPassengerName("J");
        passengerPage.selectGender("Male");
        passengerPage.setCitizenship("Belarus");
        passengerPage.setPassportNumber("123");
        passengerPage.setBirthDate(1, "January", 2024);

        $$("button").findBy(text("Continue")).click();

        $(".ant-form-item-explain-error").shouldBe(visible, Duration.ofSeconds(5));

        Assertions.assertFalse($("#payment-methods-fields").exists(),
                "Неожиданно перешли на страницу оплаты при невалидных данных");

        String errorText = $("body").getText();
        Assertions.assertTrue(errorText.contains("email") || errorText.contains("Email") ||
                        errorText.contains("invalid") || errorText.contains("age") ||
                        errorText.contains("passport") || errorText.contains("name"),
                "Не найдены ожидаемые сообщения об ошибках валидации");
    }
}
