# Курсовой проект "Сервис перевода денег"
## Описание проекта
Первым делом нам надо собрать jar архивы с нашими spring boot приложениями. Для этого в терминале в корне нашего проект выполните команду:
Для gradle: ./gradlew clean build (если пишет Permission denied тогда сначала выполните chmod +x ./gradlew)

Для maven: ./mvnw clean package (если пишет Permission denied тогда сначала выполните chmod +x ./mvnw)

Для Создания образа используйте команду docker build -t moneyapp:latest .;
Запуск контейнеров docker-compose up -d;
Настройки создания контейнеров и образов описаны в файлах ./ docker-compose.yaml и Dockerfile;
Front не обрабатывает получаемые backend id запросов, поэтому стоит заглушка на null и проведение всех операций в очереди (в тестах проблем не возникает и все работает корректно);
Данные по картам обрабатываются, ждут подтверждения оплаты и хранятся в репозитории;
Присутствует логирование с записью в файл;
Обработка исключений;
Настройка доступа с fronted к нашему серверу;
Наши модели, для получения POST запросов, обработка ->;
Контроллер TransferController.java;
Сервис TransferService.java.
## Тестирование
В проекте присутствуют JUnit тесты
Интеграционные тесты с использованием testcontainers
POST запросы
Возможны изменения http://192.168.99.100:8080/ и "http://localhost::8080" от разных версий docker и локального/запуска контейнера.
