# Подготовка к [hashcode](https://codingcompetitions.withgoogle.com/hashcode)

Решаем [задание 2019 года](problem_instructions.pdf).

Таблица с [результатами](https://codingcompetitions.withgoogle.com/hashcode/archive/2019)

Используем [optaplanner](https://www.optaplanner.org/). [Дока](https://docs.optaplanner.org/latest/optaplanner-docs/html_single/), [примеры](https://github.com/kiegroup/optaplanner/tree/master/optaplanner-examples).

## Запуск в dev

[src/main/kotlin/main.kt](src/main/kotlin/main.kt)

## Запуск под ассертами

После каждого сложного изменения
[src/main/kotlin/asserted.kt](src/main/kotlin/asserted.kt)


## benchmark

[src/main/kotlin/benchmark.kt](src/main/kotlin/benchmark.kt)

[Пример отчёта бенчмарка](https://htmlpreview.github.io/?https://github.com/SlNPacifist/hashcode-2019/blob/master/benchmark-result-example/index.html)

## Запуск в проде

Необходимые зависимости
```bash
sudo apt install maven3
```

Сборка и запуск
```bash
git clone git@github.com:SlNPacifist/hashcode-2019.git
cd ./hashcode-2019
mvn package appassembler:assemble
./target/appassembler/bin/app data/b_lovely_landscapes.txt
```

## Многопоточность

[Дока](https://docs.optaplanner.org/latest/optaplanner-docs/html_single/#multithreadedSolving)

Нет смысла запускать на разных потоках решения из разных исходных позиций – это очень незначительные улучшения. Авторы рекомендуют увеличивать скорость расчёта мувов в обычном решении за счёт переноса их в отдельные треды. Чтобы это работало, нужно имплементировать на каждом муве метод rebase – он возвращает свою "копию", которую можно использовать в другом треде.