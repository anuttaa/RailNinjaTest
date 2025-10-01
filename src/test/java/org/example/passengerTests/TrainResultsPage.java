package org.example.passengerTests;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TrainResultsPage {
    public void selectSeats() {
        $("body").shouldBe(visible, Duration.ofSeconds(15));
        String[] possibleButtonTexts = {
                "Select Seats",
                "per person"
        };

        for (String buttonText : possibleButtonTexts) {
            try {
                ElementsCollection buttons = $$("button");
                SelenideElement targetButton = buttons.findBy(text(buttonText));

                if (targetButton.exists() && targetButton.isDisplayed()) {
                    System.out.println("Найдена кнопка с текстом: '" + buttonText + "'");
                    targetButton.shouldBe(interactable, Duration.ofSeconds(3)).click();
                    return;
                }
            } catch (Exception e) {
                System.out.println("Кнопка с текстом '" + buttonText + "' не найдена: " + e.getMessage());
            }
        }
        throw new RuntimeException("Ни одна из возможных кнопок не найдена");
    }

    public void bookFlexibleTicket() {
        String[] possibleButtonTexts = {
                "Book",
                "Continue"
        };

        for (String buttonText : possibleButtonTexts) {
            try {
                ElementsCollection buttons = $$("button");
                SelenideElement targetButton = buttons.findBy(text(buttonText));

                if (targetButton.exists() && targetButton.isDisplayed()) {
                    System.out.println("Найдена кнопка с текстом: '" + buttonText + "'");
                    targetButton.shouldBe(interactable, Duration.ofSeconds(3)).click();
                    return;
                }
            } catch (Exception e) {
                System.out.println("Кнопка с текстом '" + buttonText + "' не найдена: " + e.getMessage());
            }
        }
        throw new RuntimeException("Ни одна из возможных кнопок не найдена");
    }
}
