package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Base64;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.assertThat;
import static io.restassured.RestAssured.given;

public class StationHistoryTest {

    private static final String BASE_URL = "https://back.rail.ninja/api/v1/station/history";

    @Test
    @DisplayName("Получение истории поиска с корректной cookie")
    void getSearchHistoryWithValidCookie() {
        String searchHistoryJson = """
            [
              {
                "passengers": {"adults": 1, "children": 0, "children_age": []},
                "form-mode": "basic-mode",
                "legs": {
                  "1": {
                    "departure_station": "672",
                    "arrival_station": "580",
                    "departure_date": "2025-12-31"
                  }
                }
              }
            ]""";

        String encodedCookie = encodeSearchHistoryCookie(searchHistoryJson);

        Object[] response = given()
                .cookie("search_history", encodedCookie)
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .extract()
                .as(Object[].class);

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThan(0);

        System.out.println("Получено записей истории: " + response.length);
    }

    @Test
    @DisplayName("Получение пустой истории при отсутствии cookie")
    void getEmptyHistoryWhenNoCookie() {
        Object[] response = given()
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .extract()
                .as(Object[].class);

        assertThat(response).isNotNull();
        assertThat(response.length).isEqualTo(0);
    }

    @Test
    @DisplayName("Получение истории с несколькими поисковыми запросами")
    void getHistoryWithMultipleSearches() {
        String searchHistoryJson = """
            [
              {
                "passengers": {"adults": 2, "children": 1, "children_age": [5]},
                "form-mode": "basic-mode",
                "legs": {
                  "1": {
                    "departure_station": "672",
                    "arrival_station": "580", 
                    "departure_date": "2025-12-31"
                  }
                }
              },
              {
                "passengers": {"adults": 1, "children": 0, "children_age": []},
                "form-mode": "basic-mode", 
                "legs": {
                  "1": {
                    "departure_station": "580",
                    "arrival_station": "672",
                    "departure_date": "2025-11-15"
                  }
                }
              }
            ]""";

        String encodedCookie = encodeSearchHistoryCookie(searchHistoryJson);

        Object[] response = given()
                .cookie("search_history", encodedCookie)
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .extract()
                .as(Object[].class);

        assertThat(response).isNotNull();
        assertThat(response.length).isEqualTo(2);
    }

    @Test
    @DisplayName("Проверка структуры ответа для одного поискового запроса")
    void verifyResponseStructureForSingleSearch() {
        String searchHistoryJson = """
            [
              {
                "passengers": {"adults": 1, "children": 0, "children_age": []},
                "form-mode": "basic-mode",
                "legs": {
                  "1": {
                    "departure_station": "672", 
                    "arrival_station": "580",
                    "departure_date": "2025-12-31"
                  }
                }
              }
            ]""";

        String encodedCookie = encodeSearchHistoryCookie(searchHistoryJson);

        String response = given()
                .cookie("search_history", encodedCookie)
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assertThat(response).contains("'legs'");
        assertThat(response).contains("'passengers'");
        assertThat(response).contains("'round_trip'");
        assertThat(response).contains("'complex_trip'");
        assertThat(response).contains("'departure_station'");
        assertThat(response).contains("'arrival_station'");
        assertThat(response).contains("'departure_date'");
    }

    @Test
    @DisplayName("Обработка некорректной cookie")
    void handleInvalidCookie() {
        String invalidCookie = "invalid_base64_string";

        Object[] response = given()
                .cookie("search_history", invalidCookie)
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .extract()
                .as(Object[].class);

        assertThat(response).isNotNull();
    }


    private String encodeSearchHistoryCookie(String json) {
        try {
            String base64Encoded = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
            return URLEncoder.encode(base64Encoded, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode search history cookie", e);
        }
    }


    private String decodeSearchHistoryCookie(String encodedCookie) {
        try {
            String urlDecoded = URLDecoder.decode(encodedCookie, StandardCharsets.UTF_8);
            byte[] decodedBytes = Base64.getDecoder().decode(urlDecoded);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode search history cookie", e);
        }
    }
}
