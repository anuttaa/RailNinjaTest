package org.example.passengerTests;

import com.codeborne.selenide.WebDriverRunner;

import java.util.Set;

import static com.codeborne.selenide.Selenide.sleep;

public class TabUtils {

    public static void switchToNewTabAndCloseOld() {
        String originalWindow = WebDriverRunner.getWebDriver().getWindowHandle();
        Set<String> allWindows = WebDriverRunner.getWebDriver().getWindowHandles();

        System.out.println("Всего вкладок: " + allWindows.size());

        for (String window : allWindows) {
            if (!window.equals(originalWindow)) {
                WebDriverRunner.getWebDriver().switchTo().window(originalWindow);
                WebDriverRunner.getWebDriver().close();
                System.out.println("Закрыли старую вкладку: " + originalWindow);

                WebDriverRunner.getWebDriver().switchTo().window(window);
                System.out.println("Переключились на новую вкладку: " + window);
                break;
            }
        }
        sleep(2000);
    }

    public static void waitForNewTab(int timeoutSeconds) {
        String originalTab = WebDriverRunner.getWebDriver().getWindowHandle();
        for (int i = 0; i < timeoutSeconds; i++) {
            Set<String> allTabs = WebDriverRunner.getWebDriver().getWindowHandles();
            if (allTabs.size() > 1) {
                System.out.println("Новая вкладка открылась. Всего вкладок: " + allTabs.size());
                return;
            }
            sleep(1000);
        }
        throw new RuntimeException("Новая вкладка не открылась за " + timeoutSeconds + " секунд");
    }
}
