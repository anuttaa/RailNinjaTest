package org.example.passengerTests;

import com.codeborne.selenide.*;
import org.junit.jupiter.api.Test;
import static com.codeborne.selenide.Selenide.*;

public class PassengerNameTest {

    String TEST_NAME = "HANNA HANNA";
    private MainSearchPage mainSearchPage = new MainSearchPage();
    private TrainResultsPage trainResultsPage = new TrainResultsPage();
    private PassengerPage passengerPage = new PassengerPage();

    @Test
    void testPassengerNameChanges() {
        Configuration.holdBrowserOpen = true;

        open("https://rail.ninja/");
        String originalTab = WebDriverRunner.getWebDriver().getWindowHandle();

        mainSearchPage.setDepartureStation("Mecca");
        mainSearchPage.setArrivalStation("Medina");
        mainSearchPage.setDate("2025-11-05");
        mainSearchPage.search();

        TabUtils.waitForNewTab(10);
        TabUtils.switchToNewTabAndCloseOld();

        trainResultsPage.selectSeats();
        trainResultsPage.bookFlexibleTicket();

        passengerPage.setAdultPassengerName(TEST_NAME);
        passengerPage.verifyPassengerName(TEST_NAME);
    }
}
