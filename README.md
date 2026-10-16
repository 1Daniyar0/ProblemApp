# ProblemApp

Учебное Android-приложение на Jetpack Compose для экспериментов с производительностью
и профилировщиком Android Studio.

Главный экран содержит две кнопки, которые запускают одну и ту же тяжёлую операцию —
подсчёт простых чисел до 1 000 000:

- **«Тяжёлые вычисления (Default)»** — расчёт выполняется в корутине на `Dispatchers.Default`.
  UI остаётся отзывчивым, во время работы показывается статус «Вычисляем...».
- **«Тяжёлые вычисления (Main)»** — расчёт выполняется прямо на главном потоке.
  UI зависает, и статус «Вычисляем...» не успевает отрисоваться.

Разницу удобно наблюдать в CPU Profiler, а также по времени выполнения, которое выводится на экран.

## Стек

- Kotlin 2.0, Jetpack Compose (Material 3), Kotlin Coroutines
- Android Gradle Plugin 8.13, Gradle 8.13
- minSdk 24, targetSdk / compileSdk 36, Java 11 target

## Структура модулей

| Модуль                   | Назначение                                                        |
|--------------------------|-------------------------------------------------------------------|
| `:app`                   | Точка входа: `MainActivity`, подключает экран из feature-модуля   |
| `:feature-profiler-test` | Экран `ProfilerTestScreen` с тяжёлыми вычислениями                |
| `:core-ui`               | Общая тема `ProblemAppTheme` и UI-компоненты (`ProblemButton`)    |
| `build-logic`            | Convention-плагины Gradle (application, library, compose)         |

Зависимости и версии описаны в `gradle/libs.versions.toml`.

## Сборка и запуск

Нужен JDK 17+ (например, JBR из Android Studio). Если в системе по умолчанию стоит Java 8,
укажите `JAVA_HOME` на подходящий JDK.

```bash
./gradlew assembleDebug          # собрать APK
./gradlew installDebug           # установить на устройство/эмулятор
```

## Тесты

```bash
./gradlew test                         # unit-тесты
./gradlew connectedDebugAndroidTest    # UI-тесты (нужно запущенное устройство или эмулятор)
```

UI-тесты экрана находятся в
`feature-profiler-test/src/androidTest/.../ProfilerTestScreenTest.kt`. Они проверяют начальное
состояние, состояние загрузки, результат расчёта и то, что Main-кнопка не использует фоновый
диспетчер. Для управляемого завершения фонового расчёта экран принимает параметр
`backgroundDispatcher`, который в тестах подменяется на собственный диспетчер.
