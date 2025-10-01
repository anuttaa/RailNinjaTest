package org.example.passengerTests;

import com.codeborne.selenide.*;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class PassengerPage {

    private static final String ADULT_NAME_INPUT = "#checkout-passengers-form_passengersCategories_adult_0_full_name";
    private static final String PASSENGER_NAME_SPAN = "span.sc-cf59c1f-1.dfrRB";
    private static final String EMAIL_INPUT = "#checkout-passengers-form_clientDetails_user_email";
    private static final String CONFIRM_EMAIL_INPUT = "#checkout-passengers-form_clientDetails_confirm_user_email";
    private static final String CITIZENSHIP_INPUT = "#checkout-passengers-form_passengersCategories_adult_0_citizenship";
    private static final String PASSPORT_INPUT = "#checkout-passengers-form_passengersCategories_adult_0_id_number";
    private static final String DATE_BASE_SELECTOR = "div[id='checkout-passengers-form_passengersCategories_adult_0_dob']";

    private static final ScrollStrategy DAY_STRATEGY = new ScrollStrategy(150, 300, 20);
    private static final ScrollStrategy MONTH_STRATEGY = new ScrollStrategy(100, 300, 15);
    private static final ScrollStrategy YEAR_STRATEGY = new ScrollStrategy(0, 0, 0);

    public PassengerPage setAdultPassengerName(String name) {
        $(ADULT_NAME_INPUT).click();
        $(ADULT_NAME_INPUT).setValue(name);
        return this;
    }

    public void verifyPassengerName(String expectedName) {
        $(PASSENGER_NAME_SPAN).shouldHave(text(expectedName));
        String actualName = $(PASSENGER_NAME_SPAN).getText();
        Assertions.assertTrue(actualName.contains(expectedName));
    }

    public void setEmail(String email) {
        $(EMAIL_INPUT).click();
        $(EMAIL_INPUT).setValue(email);
        $(CONFIRM_EMAIL_INPUT).setValue(email);
    }

    public void selectGender(String gender) {
        $x("//label[span[text()='" + gender + "']]").click();
    }

    public void setCitizenship(String citizenship) {
        $(CITIZENSHIP_INPUT).click();
        $(CITIZENSHIP_INPUT).setValue(citizenship).pressEnter();
    }

    public void setPassportNumber(String passportNumber) {
        $(PASSPORT_INPUT).click();
        $(PASSPORT_INPUT).setValue(passportNumber);
    }

    public void setBirthDate(int day, String month, int year) {
        selectDatePart(String.valueOf(day), "day");
        sleep(1000);
        selectDatePart(month, "month");
        sleep(1000);
        selectDatePart(String.valueOf(year), "year");
    }

    private void selectDatePart(String value, String type) {
        SelenideElement targetField = findDateField(getFieldIndex(type));
        targetField.shouldBe(interactable, Duration.ofSeconds(5)).click();

        waitForDropdownVisible();
        SelenideElement activeDropdown = $("div.ant-select-dropdown:not(.ant-select-dropdown-hidden)");

        if ("year".equals(type)) {
            selectYearWithSmartStrategy(activeDropdown, value);
        } else {
            selectWithSmartScroll(activeDropdown, value, type);
        }

        closeDropdown();
        sleep(1000);
    }

    private int getFieldIndex(String type) {
        return switch (type) {
            case "day" -> 0;
            case "month" -> 1;
            case "year" -> 2;
            default -> throw new IllegalArgumentException("Unknown field type: " + type);
        };
    }

    private void selectWithSmartScroll(SelenideElement dropdown, String value, String type) {
        SelenideElement scrollContainer = findScrollContainer(dropdown);
        ScrollStrategy strategy = getScrollStrategy(type);

        if (tryFindAndClickOption(dropdown, value, type)) return;

        if (scrollWithStrategy(scrollContainer, dropdown, value, type, strategy.scrollStep, strategy.maxAttempts, strategy.delay)) {
            return;
        }

        if (scrollWithStrategy(scrollContainer, dropdown, value, type, -strategy.scrollStep, 20, strategy.delay)) {
            return;
        }

        throw new RuntimeException(type + " '" + value + "' not found");
    }

    private boolean scrollWithStrategy(SelenideElement scrollContainer, SelenideElement dropdown,
                                       String value, String type, int scrollStep, int maxAttempts, int delay) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            executeJavaScript("arguments[0].scrollTop += " + scrollStep + ";", scrollContainer);
            sleep(delay);

            if (tryFindAndClickOption(dropdown, value, type)) {
                return true;
            }
        }
        return false;
    }

    private void selectYearWithSmartStrategy(SelenideElement dropdown, String value) {
        SelenideElement scrollContainer = findScrollContainer(dropdown);
        int targetYear = Integer.parseInt(value);

        for (int attempt = 1; attempt <= 15; attempt++) {
            executeJavaScript("arguments[0].scrollTop -= 300;", scrollContainer);
            sleep(200);

            if (tryFindAndClickOption(dropdown, value, "year")) return;

            if (attempt % 3 == 0 && shouldStopUpScroll(dropdown)) {
                break;
            }
        }

        for (int attempt = 1; attempt <= 20; attempt++) {
            executeJavaScript("arguments[0].scrollTop += 200;", scrollContainer);
            sleep(200);

            if (tryFindAndClickOption(dropdown, value, "year")) return;
        }

        executeJavaScript("arguments[0].scrollTop = 0;", scrollContainer);
        sleep(1000);
        if (tryFindAndClickOption(dropdown, value, "year")) return;

        executeJavaScript("arguments[0].scrollTop = arguments[0].scrollHeight;", scrollContainer);
        sleep(1000);
        if (tryFindAndClickOption(dropdown, value, "year")) return;

        findAndClickOptionInAll(dropdown, value, "year");
    }

    private boolean shouldStopUpScroll(SelenideElement dropdown) {
        ElementsCollection visibleOptions = dropdown.$$("div.ant-select-item.ant-select-item-option").filter(visible);
        if (visibleOptions.isEmpty()) return false;

        String firstVisibleYear = visibleOptions.first().getText();
        return firstVisibleYear.matches("\\d+") && Integer.parseInt(firstVisibleYear) < 1950;
    }

    private void findAndClickOptionInAll(SelenideElement dropdown, String value, String type) {
        ElementsCollection allOptions = dropdown.$$("div.ant-select-item.ant-select-item-option");
        SelenideElement targetOption = findOption(allOptions, value, type);

        if (targetOption.exists()) {
            executeJavaScript("arguments[0].scrollIntoView({block: 'center'});", targetOption);
            sleep(500);
            targetOption.click();
        } else {
            throw new RuntimeException("Year '" + value + "' not found");
        }
    }

    private ScrollStrategy getScrollStrategy(String type) {
        return switch (type) {
            case "day" -> DAY_STRATEGY;
            case "month" -> MONTH_STRATEGY;
            case "year" -> YEAR_STRATEGY;
            default -> new ScrollStrategy(150, 300, 20);
        };
    }

    private boolean tryFindAndClickOption(SelenideElement dropdown, String value, String type) {
        ElementsCollection visibleOptions = dropdown.$$("div.ant-select-item.ant-select-item-option").filter(visible);
        SelenideElement targetOption = findOption(visibleOptions, value, type);

        if (targetOption.exists()) {
            targetOption.click();
            return true;
        }
        return false;
    }

    private SelenideElement findScrollContainer(SelenideElement dropdown) {
        String[] scrollSelectors = {".rc-virtual-list-holder", "[class*='scroll']", "[class*='virtual']", "> div"};

        for (String selector : scrollSelectors) {
            ElementsCollection containers = dropdown.$$(selector);
            if (!containers.isEmpty()) {
                SelenideElement container = containers.first();
                Boolean hasScroll = executeJavaScript(
                        "return arguments[0].scrollHeight > arguments[0].clientHeight", container
                );
                if (Boolean.TRUE.equals(hasScroll)) {
                    return container;
                }
            }
        }
        return dropdown;
    }

    private SelenideElement findOption(ElementsCollection options, String value, String type) {
        if ("month".equals(type)) {
            SelenideElement option = options.findBy(text(value));
            if (option.exists()) return option;

            String shortMonth = value.length() > 3 ? value.substring(0, 3) : value;
            option = options.findBy(text(shortMonth));
            if (option.exists()) return option;
        }
        return options.findBy(text(value));
    }

    private SelenideElement findDateField(int index) {
        String[] selectors = {
                DATE_BASE_SELECTOR + " .ant-select-selector",
                DATE_BASE_SELECTOR + " .ant-select",
                DATE_BASE_SELECTOR + " .ant-picker-input input"
        };

        for (String selector : selectors) {
            ElementsCollection fields = $$(selector);
            if (fields.size() > index) {
                SelenideElement field = fields.get(index);
                if (field.exists() && field.isDisplayed()) {
                    return field;
                }
            }
        }
        throw new RuntimeException("Could not find date field at index " + index);
    }

    private void closeDropdown() {
        $("body").click();
        sleep(500);
    }

    private void waitForDropdownVisible() {
        $$("div.ant-select-dropdown")
                .findBy(not(cssClass("ant-select-dropdown-hidden")))
                .shouldBe(visible, Duration.ofSeconds(5));
    }

    private static class ScrollStrategy {
        final int scrollStep, delay, maxAttempts;

        ScrollStrategy(int scrollStep, int delay, int maxAttempts) {
            this.scrollStep = scrollStep;
            this.delay = delay;
            this.maxAttempts = maxAttempts;
        }
    }
}
