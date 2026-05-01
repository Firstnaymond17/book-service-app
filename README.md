# Book Service

CRUD сервис для управления книгами. Spring Boot, H2, Actuator, Prometheus, Grafana.

## Запуск в IDEA

1. Открыть проект через File → Open
2. Дождаться загрузки зависимостей Maven
3. Запустить BookServiceApplication
4. Приложение на http://localhost:8080
5. H2 консоль: http://localhost:8080/h2-console, JDBC URL: jdbc:h2:mem:booksdb, user: sa

## Запуск мониторинга

cd metrics
docker compose up -d

Prometheus: http://localhost:9090
Grafana: http://localhost:3000 (admin/admin)

## API

| Метод | URL | Описание |
|---|---|---|
| GET | /api/books | Все книги |
| GET | /api/books/{id} | Книга по ID |
| POST | /api/books | Создать книгу |
| PUT | /api/books/{id} | Обновить книгу |
| DELETE | /api/books/{id} | Удалить книгу |
| GET | /api/books/heavy | Тяжёлое вычисление |

## Анализ дампов потоков

Дампы сняты через jstack во время выполнения /api/books/heavy?iterations=200000000.

Формула: Нагрузка = (cpu_ms / (elapsed x 1000)) x 100%

| Название потока | elapsed (ms) | cpu_ms | Нагрузка |
|---|---|---|---|
| DestroyJavaVM | 147000 | 5968.75 | 4.06% |
| http-nio-8080-exec-3 | 147030 | 3812.50 | 2.59% |
| RMI TCP Connection(3) | 146570 | 3046.88 | 2.08% |

Поток http-nio-8080-exec-3 выполнял BookService.heavyComputation() — видно из стектрейса. Он потреблял наибольшую нагрузку среди рабочих потоков.

Heap dump снят через jmap. Размер: 81 МБ. Утечек памяти не обнаружено.

## Grafana

### JVM Micrometer (ID: 4701)
<img width="1920" height="943" alt="Снимок" src="https://github.com/user-attachments/assets/31533e3c-19ea-4e7a-bbbb-189258a104be" />
<img width="1920" height="954" alt="Снимок2" src="https://github.com/user-attachments/assets/d29c3d55-561d-4384-bb1f-adbb5971c372" />

### Book Service Custom Dashboard
<img width="1216" height="931" alt="Снимок3" src="https://github.com/user-attachments/assets/b491ecc0-dd32-4fa8-a589-7df3ef8ad3da" />

| Панель | PromQL | Описание |
|---|---|---|
| Среднее время ответа по URL | sum by(uri) (rate(http_server_requests_seconds_sum{application="book-service"}[5m])) / sum by(uri) (rate(http_server_requests_seconds_count{application="book-service"}[5m])) | Среднее время ответа по каждому URL за 5 минут |
| Использование памяти JVM | jvm_memory_used_bytes{application="book-service"} | Использование памяти по областям heap/non-heap |
| HTTP запросы по статусу | sum by(status) (rate(http_server_requests_seconds_count{application="book-service"}[1m])) | Количество запросов в секунду по HTTP статусу |

