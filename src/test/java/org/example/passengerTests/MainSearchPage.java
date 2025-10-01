package org.example.passengerTests;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.ElementClickInterceptedException;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class MainSearchPage {
    public MainSearchPage setDepartureStation(String station) {
        $x("//*[@id='departure_station']").click();
        $x("//*[@id='departure_station']").setValue(station);
        SelenideElement depDropdown = $$("div.ant-select-dropdown").last().shouldBe(visible);
        depDropdown.$$("div.ant-select-item-option").findBy(text(station)).click();
        return this;
    }

    public MainSearchPage setArrivalStation(String station) {
        SelenideElement arrivalField = $x("//*[@id='arrival_station']");

        arrivalField.shouldBe(interactable, Duration.ofSeconds(5));

        try {
            arrivalField.click();
        } catch (ElementClickInterceptedException e) {
            executeJavaScript("arguments[0].click();", arrivalField);
        }

        arrivalField.setValue(station);

        SelenideElement dropdown = $$("div.ant-select-dropdown")
                .findBy(not(cssClass("ant-select-dropdown-hidden")))
                .shouldBe(visible, Duration.ofSeconds(5));

        dropdown.$$("div.ant-select-item-option")
                .shouldBe(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(3))
                .findBy(text(station))
                .click();

        return this;
    }
    public MainSearchPage setDate(String date) {
        $("#search-form-rn-modern > div.ant-form-item.css-106m6a8.ant-form-item-has-success > div > div > div > div > div").click();
        $$("div.ant-picker-body table.ant-picker-content tbody tr td[title='" + date + "']").first().click();
        return this;
    }

    public void search() {
        $("#search-form-rn-modern > button").click();
    }
}
