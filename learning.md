# Общий сценарий

- Запуск одного сервера и актеров в одной jvm
- Запуск второго сервера
- Запуск через nginx
- Запуск актеров в разных jvm (запуск не всех сразу - очередь ожидает, видно last polled time)
- Запуск актера который ломается переодически, выявление проблем, замена на нормального и завершение всех процессов

## Подготовка

Запускаем монгу

./mongodstart.sh

Пароль: qwerty

Проверяем что работает. В другой консоли подсоединяемся к ней.

mongo

    use taskurotta
    db stats


## Запуск одного сервера и актеров в одной jvm

### Запуск сервера

sudo bash
cd /opt/taskurotta

Извлечь hz-mongo.yml из taskurotta-8.0.0.jar

unzip -x taskurotta-8.0.0.jar hz-mongo.yml

Смотрим структуру файла (properties - настройка кластера, веб сервер - порт, логирование)

- Меняем месторасположение логов на logs/...
- Меняем порт сервера (server > connector > port) на 8081

Запускаем сервер

java -Xmx128m -jar taskurotta-0.8.0.jar server hz-mongo.yml

Идем в консоль

http://lovalhost:8081

### Старт процессов

Идем в директорию /opt/taskurotta/process

Стартуем 901 процесс

java -cp getstarted-process-0.1.0.jar ru.taskurotta.example.starter.NotificationModule http://localhost:8081 901

Смотрим список очередей в консоли

### Старт координатора

Извлекаем файл config-decider.yml

unzip -x getstarted-process-0.1.0.jar config-decider.yml

О структуре файла....

Правим путь лог файла

Запускаем

java -Xmx64m -jar getstarted-process-0.1.0.jar -f config-decider.yml

Смотрим в веб консоль - видим изменение в очередях

После выполнения смотрим в терминал taskurotta - видим long poll

Оставляем работать

### Старт исполнителей

Извлекаем файл config-workers.yml

Правим порт у endpoint
Правим путь до лог файла

Запускаем

java -Xmx64m -jar getstarted-process-0.1.0.jar -f config-workers.yml

Смотрим консоль на предмет опрашивания очередей

Останавливаем исполнителей и координатора.

Смотрим бегло монгу.
- статистику бд
- список коллекций
- количество элементов в коллекции
- список объектов коллекции

Потом чистим
db.dropDatabase()

## Запуск актеров с ошибкой

Запускаем 901 процесс

Запускаем координатора

Запускаем исполнителя с ошибкой

java -Xmx64m -jar getstarted-process-error-0.1.0.jar -f config-workers.yml

### Чиним окружение

Смотрим сломанные процессы в консоли

Останавливаем обработку задач актера с проблемным окружение
Видим:
- перестало расти число сломанных процессов
- обработка идет но очередь остановленного исполнителя не разбирается, при этом
видно что он обращается за задачами

Смотрим что за ошибка (смотрим типы, процессы, сообщение об ошибке, stack trace)

Как будто починили, перезапускаем задачи процесса (IllegalStateException) и включаем обработку задач
актера

### Чиним актеров

Останавливаем обработку задач сломанного актера

Останавливаем исполнителей

Запускаем исполнителей без ошибки

Восстанавливаем обработку задач сломанного актера

Дожидаемся завершения всех процессов




