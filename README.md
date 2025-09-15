# Руководство по развертыванию Kafka-кластера

В этом руководстве объясняется, как развернуть кластер Kafka с использованием Docker Compose, проверить его работу и мониторить с помощью Kafka UI.

## Содержание
- [Предварительные требования](#предварительные-требования)
- [Развертывание](#развертывание)
- [Проверка работоспособности](#проверка-работоспособности)
- [Параметры конфигурации](#параметры-конфигурации)
- [Использование Kafka UI](#использование-kafka-ui)
- [Устранение неполадок](#устранение-неполадок)

## Предварительные требования

- Установленные Docker и Docker Compose
- Git (опционально, для клонирования репозитория)
- Не менее 4 ГБ оперативной памяти, доступной для Docker

## Развертывание

### Шаг 1: Клонирование репозитория или создание docker-compose.yml

```bash
git clone https://github.com/yourusername/kafka-cluster.git
cd kafka-cluster
```

Или создайте файл `docker-compose.yml` со следующим содержимым:

```yaml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.0.1
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"
  kafka:
    image: confluentinc/cp-kafka:7.0.1
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    ports:
      - "9092:9092"
  ui:
    image: provectuslabs/kafka-ui:v0.7.0
    ports:
      - "8080:8080"
    environment:
      - KAFKA_CLUSTERS_0_BOOTSTRAP_SERVERS=kafka-0:9092
      - KAFKA_CLUSTERS_0_NAME=kraft
```

### Шаг 2: Запуск Kafka-кластера

```bash
docker-compose up -d
```

Эта команда запускает все сервисы в фоновом режиме.

## Проверка работоспособности

### Шаг 1: Проверка запуска контейнеров

```bash
docker-compose ps
```

Все контейнеры должны иметь статус "Up".

### Шаг 2: Проверка работы Zookeeper

```bash
docker exec -it zookeeper bash -c "echo stat | nc localhost 2181"
```

Вы должны увидеть статистику Zookeeper, включая режим работы, количество соединений и задержку.

### Шаг 3: Проверка работы Kafka-брокера

```bash
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list
```

Эта команда должна выполниться без ошибок, возможно, показав существующие топики.

### Шаг 4: Создание тестового топика

```bash
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --create --topic test-topic --partitions 3 --replication-factor 1
```

### Шаг 5: Отправка и получение сообщений

В одном терминале запустите консольный продюсер:
```bash
docker exec -it kafka kafka-console-producer --bootstrap-server localhost:9092 --topic test-topic
```

Введите несколько сообщений и нажмите Ctrl+D для выхода.

В другом терминале запустите консольный потребитель:
```bash
docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic test-topic --from-beginning
```

Вы должны увидеть отправленные вами сообщения.

## Параметры конфигурации

### Конфигурация Zookeeper

| Параметр | Значение | Описание |
|-----------|-------|-------------|
| `ZOOKEEPER_CLIENT_PORT` | 2181 | Порт, который клиенты используют для подключения к ZooKeeper |
| `ZOOKEEPER_TICK_TIME` | 2000 | Базовая единица времени в миллисекундах, используемая для сердцебиений и таймаутов |

### Конфигурация Kafka

| Параметр | Значение | Описание |
|-----------|-------|-------------|
| `KAFKA_BROKER_ID` | 1 | Уникальный идентификатор для брокера Kafka |
| `KAFKA_ZOOKEEPER_CONNECT` | zookeeper:2181 | Строка подключения к ZooKeeper |
| `KAFKA_ADVERTISED_LISTENERS` | PLAINTEXT://localhost:9092 | Адрес, который брокер сообщает клиентам для подключения |
| `KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR` | 1 | Фактор репликации для топика смещений |
| `KAFKA_TRANSACTION_STATE_LOG_MIN_ISR` | 1 | Минимальное количество реплик для журнала состояния транзакций |
| `KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR` | 1 | Фактор репликации для журнала состояния транзакций |

### Конфигурация Kafka UI

| Параметр | Значение | Описание |
|-----------|-------|-------------|
| `KAFKA_CLUSTERS_0_NAME` | local | Имя кластера Kafka в интерфейсе |
| `KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS` | kafka:9092 | Bootstrap-серверы для кластера Kafka |
| `KAFKA_CLUSTERS_0_ZOOKEEPER` | zookeeper:2181 | Строка подключения к ZooKeeper |

## Использование Kafka UI

Kafka UI предоставляет веб-интерфейс для управления и мониторинга вашего кластера Kafka.

### Шаг 1: Доступ к интерфейсу

Откройте браузер и перейдите по адресу:
```
http://localhost:8080
```

### Шаг 2: Изучение панели управления

Панель управления показывает:
- Состояние кластера
- Информацию о брокерах
- Список топиков
- Группы потребителей

### Шаг 3: Создание топика

1. Нажмите на "Topics" в левой боковой панели
2. Нажмите кнопку "Add a Topic"
3. Заполните имя топика, количество партиций и фактор репликации
4. Нажмите "Create"

### Шаг 4: Отправка сообщений

1. Выберите ваш топик из списка топиков
2. Перейдите на вкладку "Messages"
3. Используйте форму "Produce Message" для отправки сообщений
4. Вы можете указать ключ, значение и заголовки

### Шаг 5: Мониторинг групп потребителей

1. Нажмите на "Consumer Groups" в левой боковой панели
2. Просмотрите активные группы потребителей и их отставание (lag)

## Устранение неполадок

### Контейнер не запускается

Проверьте логи на наличие конкретных ошибок:
```bash
docker-compose logs kafka
```

### Не удается подключиться к Kafka

Убедитесь, что advertised listeners настроены правильно:
```bash
docker exec -it kafka bash -c "cat /etc/kafka/server.properties | grep advertised"
```

### Топики не видны в Kafka UI

Проверьте, может ли Kafka UI подключиться к Kafka:
```bash
docker-compose logs kafka-ui
```

### Проблемы с производительностью

Проверьте использование ресурсов:
```bash
docker stats
```

---

Эта настройка предназначена для разработки и тестирования. Для производственных сред рекомендуется:
- Использовать несколько брокеров Kafka
- Настроить безопасность должным образом
- Обеспечить сохранение данных
- Настроить мониторинг и оповещения