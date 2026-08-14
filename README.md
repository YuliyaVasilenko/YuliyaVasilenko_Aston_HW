Реализована микросервисная система управления пользователями и уведомлениями.

Микросервисы:

1. user-service (порт 8081) — CRUD‑операции для сущности User, интеграция с Kafka:
    * REST API для CRUD‑операции для сущности User;
    * отправка сообщения в Kafka при создании/удалении пользователя;
    * поддержка HATEOAS‑ссылок;
    * документация Swagger/OpenAPI.

2. notification-service (порт 8082) — отправка email‑уведомлений:
    * обработка Kafka‑событий для отправки email;
    * REST API для прямой отправки уведомлений (без Kafka);
    * документация Swagger/OpenAPI.

3. api-gateway (порт 8088) — единая точка входа (Spring Cloud Gateway) с реализацией Circuit Breaker (Resilence4j)
   для переключения в случае недоступности сервисов;

4. discovery-service (порт 8761) — регистрация всех сервисов (Spring Cloud Netflix Eureka Server);

5. config-server (порт 8888) — внешнее управление конфигурациями для всех сервисов через отдельный git-репозиторий
   (подключение к базе данных, настройки Kafka, параметры Eureka Client, логирование, Actuator,
   настройки mail, маршрутизация Gateway, настройки OpenAPI).

Использованы технологии: Spring Boot 4.0.5, Spring Web MVC, Spring Data JPA, Spring Validation; Spring Mail;
Kafka (событийная модель); PostgreSQL (база данных); Swagger/OpenAPI (документация); HATEOAS (для user-service);
Spring Cloud: Gateway, Eureka, Config Server, Resilience4j (Circuit Breaker); Сборка Maven.

Применены паттерны проектирования:

* API Gateway (Spring Cloud Gateway) — единая точка входа;
* Service Discovery (Eureka) — автоматическое обнаружение сервисов;
* Circuit Breaker (Resilience4j) — защита от сбоев сервисов;
* External Configuration (Config Server) — централизованное управление конфигурациями;
* HATEOAS — навигация по ресурсам с помощью гиперссылок;
* Event‑Driven Architecture (Kafka) — асинхронная коммуникация.

Использованы инструменты тестирования: JUnit 5; Mockito; Spring Boot Test; Spring MVC Test;
Testcontainers (интеграционные тесты с PostgreSQL и Kafka); GreenMail (интеграционные тесты email‑отправок).

Для запуска всех тестов:
./mvnw test

Для запуска проекта:

* В корневой директории проекта выполнить: docker compose up -d
* Запустить сервисы в следующем порядке:
    1. Config Server (ConfigServiceApplication);
    2. Discovery Service (DiscoveryServiceApplication);
    3. Api-Gateway (ApiGatewayApplication);
    4. User Service (UserServiceApplication);
    5. Notification Service (NotificationServiceApplication).

Проверка работоспособности:

1. Eureka Dashboard: http://localhost:8761 — статус всех зарегистрированных сервисов;
2. MailHog: http://localhost:8025 - веб‑интерфейс для просмотра отправленных email-писем
3. Swagger UI User Service: http://localhost:8081/api/swagger-ui.html;
4. Swagger UI Notification Service: http://localhost:8082/api/swagger-ui.html;
5. Документация OpenAPI JSON: User Service: http://localhost:8081/api/v3/api-docs
6. Документация OpenAPI JSON: Notification Service: http://localhost:8082/api/v3/api-docs
7. Actuator endpoints: 'http://localhost:порт/actuator' и 'http://localhost:порт/api/actuator/' для 8081 и 8082 -
   все доступные endpoints для микросервиса: health, info, loggers, mappings для api-gateway;
8. Логирование: подробное логирование операций в каждом сервисе.

Тестирование API:
Все запросы предпочтительно отправлять через API Gateway (http://localhost:8088) для корректного отображения
в случае, если сервисы недоступны, но также возможна отправка напрямую в сервисы
http://localhost:8081/api/users/ и http://localhost:8082/api/notifications.

Примеры запросов:

1. CREATE — создать пользователя
   curl -X POST \
   -H "Content-Type: application/json" \
   --data '{
   "name": "Test Name",
   "email": "test@email.ru",
   "age": 25
   }' \
   http://localhost:8088/api/users

Результат:

* запись в БД;
* событие в Kafka: {"operation": "CREATE", "email": "test@email.ru"};
* email‑уведомление: "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан".

2. GET — получить пользователя по ID
   curl -X GET http://localhost:8088/api/users/1

3. GET — получить всех пользователей
   curl -X GET http://localhost:8088/api/users

4. PUT — обновить пользователя
   curl -X PUT \
   -H "Content-Type: application/json" \
   --data '{
   "name": "Updated Name",
   "email": "updated@email.ru",
   "age": 30
   }' \
   http://localhost:8088/api/users/1

5. DELETE — удалить пользователя
   curl -X DELETE http://localhost:8088/api/users/1

Результат:

* удаление записи из БД;
* событие в Kafka: {"operation": "DELETE", "email": "test@email.ru"};
* email‑уведомление: "Здравствуйте! Ваш аккаунт был удалён".

6. Прямая отправка уведомления (без Kafka)

curl -X POST \
http://localhost:8088/api/notifications?email=test@email.ru&operation=CREATE

curl -X POST \
http://localhost:8088/api/notifications?email=test@email.ru&operation=DELETE

7. Если User Service недоступен (http://localhost:8088/api/users/**), при отправке любого запроса выводится сообщение:
   "User Service is currently unavailable. Please try again later."

8. Если Notification Service недоступен (http://localhost:8088/api/notifications/**), выводится сообщение:
   "Service is currently unavailable. Please try again later."

