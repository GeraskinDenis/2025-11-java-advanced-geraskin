# Homework 3
Оптимизация SerialGC.

__Цель:__
Данная работа позволит закрепить изученный материал по работе с параметрами GC и с инструментами мониторинга работы GC.

__Описание/Пошаговая инструкция выполнения домашнего задания:
1. Установить параметры запуска приложения `-XX:+UseSerialGC -Xms128m -Xmx128m -Xlog:gc*::time`
2. Запустить приложение
3. В логах найти хотя бы одно `Pause Full`
4. Настроить -XX:SurvivorRatio=? или/и -XX:NewRatio=? Так что бы приложение не запускало полную сборку до конца своего выполнения

__Критерии оценки:__
1. Во время работы приложения не запускается полная сборка.
2. По выполнению ДЗ укажите какие параметры VM выставили.

# Solution
Запуск приложения с параметрами `-XX:SurvivorRatio` и `-XX:NewRatio` по умольчанию:
```shell
# -Xms128m - HEAP
# -Xmx128m - max HEAP
java -XX:+UseSerialGC -Xms128m -Xmx128m -Xlog:gc*::time -classpath target/classes ru.geraskindenis.Practice
```
GC вызвал `Pause Full` 22 раза.

---
### Запуск приложения с параметрами `-XX:NewRatio=3`:
```shell
# -Xms128m - HEAP
# -Xmx128m - Max HEAP
# -XX:NewRatio= - Old/Yang
java -XX:+UseSerialGC -Xms128m -Xmx128m -XX:NewRatio=3 -Xlog:gc*::time -classpath target/classes ru.geraskindenis.Practice
```
GC вызвал `Pause Full` 26 раза.

---
### Запуск приложения с параметрами `-XX:NewRatio=1`:
```shell
# -Xms128m - HEAP
# -Xmx128m - Max HEAP
# -XX:NewRatio= - Old/Yang
java -XX:+UseSerialGC -Xms128m -Xmx128m -XX:NewRatio=1 -Xlog:gc*::time -classpath target/classes ru.geraskindenis.Practice
```
GC вызвал `Pause Full` 14 раза.

---
### Запуск приложения с параметрами `-XX:SurvivorRatio=1` и `-XX:NewRatio=1`: 
```shell
# -Xms128m - HEAP
# -Xmx128m - Max HEAP
# -XX:SurvivorRatio= - Eden/Survivor
# -XX:NewRatio= - Old/Yang
java -XX:+UseSerialGC -Xms128m -Xmx128m -XX:NewRatio=1 -XX:SurvivorRatio=1 -Xlog:gc*::time -classpath target/classes ru.geraskindenis.Practice
```
GC не вызвал `Pause Full`.
Увеличение блока `YangGen` и увеличение блоков `Survivor` позволило избежать в GC `Pause Full`.

---
__Владимир Иванов:__
Предлагаю создать пулл реквест, привести в нем скриншоты запусков с разными параметрами,
описать как подобрал нужные, из чего исходил.

__Ответ:__
Владимир, скриншотов не делал. Сначала я исходил из того, что `Pause Full` запускается при заполнении блока
`OldGen` и поэтому решил попробовать увеличить этот блок с помощью параметра `-XX:NewRatio`, но получал обратный эффект. 
В коде новые объекты имеют очень короткий цикл жизни и GC скорее всего не успевает переметить их в `OldGen`.
Так как объекты создаются в блоках `Eden` и далее перемещаются `S0` и `S1` - я решил наоборот увеличить их.
Таким образом я добился отсутсвие вызовов GC `Pause Full` максимально увеличив блоки `Eden`, `S0` и `S1`.