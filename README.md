Реализован микросервис (user-service), поддерживающий базовые операции CRUD для сущности User.
При создании и удалении пользователя отправляется событие в Kafka.

Реализован микросервис(notification-service) для отправки сообщения на почту при удалении или добавлении пользователя.
При получении события из Kafka отправляется email пользователю.
Также отдельно реализовано REST API для отправки email напрямую (без Kafka).

Добавлена Swagger-документация (Springdoc OpenAPI) для обоих микросервисов для тестирования API через веб-интерфейс:
localhost:8081/api/swagger-ui.html, localhost:8082/api/swagger-ui.html.
Документация: localhost:8081/api/v3/api-docs, localhost:8082/api/v3/api-docs.
Добавлена поддержка HATEOAS-ссылок для навигации по ресурсам (только для user-service).

Использованы модули spring: boot (4.0.5), webmvc, data, validation, kafka, mail, hateoas.
База данных — PostgreSQL. Система сборки Maven.

Написаны тесты с использованием SpringBootTest, SpringBoot-WebMVC-Test, JUnit 5, Mockito;
интеграционные тесты с RestTemplate, Testcontainers, GreenMail (SMTP сервер для тестов).

Для запуска проекта:

1. В корневой директории проекта выполнить: docker compose up -d
2. Запустить UserServiceApplication
3. Запустить NotificationServiceApplication

Для запуска всех тестов:
./mvnw test

Проверка через Postman:

1. CREATE - создать запись
   curl -X POST \
   -H "Content-Type: application/json" \
   --data '{
   "name": "Test Name",
   "email": "test@email.ru",
   "age": 25
   }' \
   http://localhost:8081/api/users

В Kafka отправляется: { "operation": "CREATE", "email": "test@mail.com" }
Отправляется email: Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.

Отдельное API (без Kafka, только отправка уведомления на email):
POST http://localhost:8082/api/notifications?email=test@mail.com&operation=CREATE

2. DELETE — удалить запись
   curl -X DELETE http://localhost:8081/api/users/1

В Kafka отправляется: { "operation": "DELETE", "email": "test@mail.com" }
Отправляется email: Здравствуйте! Ваш аккаунт был удалён.

Отдельное API (без Kafka, только отправка уведомления на email):
POST http://localhost:8082/api/notifications?email=test@mail.com&operation=DELETE

3. GET — получить запись
   curl -X GET http://localhost:8081/api/users/1

4. GET — получить все записи
   curl -X GET http://localhost:8081/api/users

5. PUT/PATCH — обновить запись (UPDATE)
   curl -X PUT \
   -H "Content-Type: application/json" \
   --data '{
   "name": "Updated Name",
   "email": "updated@email.ru",
   "age": 30
   }' \
   http://localhost:8081/api/users/1