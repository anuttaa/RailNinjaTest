Данный проект представляет собой фреймворк для автоматизированного тестирования веб-сайта Rail.Ninja. 
Фреймворк включает UI-тесты (Selenide) и API-тесты (RestAssured) для проверки функциональности поиска и бронирования железнодорожных билетов.

Технологический стек

Java 21 - язык программирования

JUnit 5 - фреймворк для тестирования

Selenide 7.10.1 - фреймворк для UI-тестирования

RestAssured 5.5.6 - библиотека для API-тестирования

Maven - система сборки

AssertJ 3.24.2 - библиотека для утверждений

Jackson 2.17.2 - работа с JSON

Установка и запуск
Предварительные требования
Java 21 или выше, Maven 3.6+

Клонирование репозитория
git clone <https://github.com/anuttaa/RailNinjaTest>
cd "папка проекта"

Сборка проекта
mvn clean compile

# Запуск всех тестов
mvn test

# Запуск конкретных тестов
# Тест заголовка Adult passenger 1
mvn test -Dtest=PassangerTests.PassengerNameTest

# Тест рейсов
mvn test -Dtest=TimetableTest.TimetableApiTest

# Тест формы данных пассажира
mvn test -Dtest=FormTest

# Тест истории поисков маршрутов
mvn test -Dtest=StationHistoryTest
