
# MyGrade (Зачетка)

**MyGrade** — это Android-приложение для студентов, позволяющее отслеживать успеваемость в реальном времени. Приложение синхронизирует оценки напрямую из **Google Таблиц** (Google Sheets), отображает прогресс выполнения и сохраняет данные локально для офлайн-доступа.

>  **Проект находится в стадии активного рефакторинга:** Переход с монолитной архитектуры на **Clean Architecture + MVVM**.

---

##  Скриншоты
<img width="576" height="1280" alt="image" src="https://github.com/user-attachments/assets/3c4dfcf4-531e-47f5-ac27-98ec87d70c60" /> <img width="576" height="1280" alt="image" src="https://github.com/user-attachments/assets/f93d7e71-e1c4-46f7-a167-6ba4250b2dbb" /> <img width="576" height="1280" alt="image" src="https://github.com/user-attachments/assets/85db147e-0e89-458e-9c27-04e827d71957" />




---

##  Технологический стек

Проект демонстрирует использование современных инструментов разработки под Android (Java) и следование принципам "Чистой Архитектуры".

*   **Language:** Java
*   **Architecture:** Clean Architecture (Domain / Data / Presentation layers)
*   **Design Pattern:** MVVM (Model-View-ViewModel)
*   **Dependency Injection:** Hilt (Dagger)
*   **Network:** Retrofit 2 + Gson (REST API interaction)
*   **Local Storage:** Room Database (SQLite abstraction)
*   **Async/Reactive:** LiveData, Executors
*   **UI:** RecyclerView, Material Design Components

---

##  Архитектура

Приложение разделено на три независимых слоя согласно принципам Clean Architecture:

1.  **Domain Layer** (Java Module):
    *   Содержит бизнес-логику и сущности (`Subject`).
    *   Определяет интерфейсы репозиториев (`SubjectRepository`).
    *   Не имеет зависимостей от Android SDK.

2.  **Data Layer** (Android Module):
    *   Реализует интерфейсы из Domain слоя.
    *   **Local:** База данных Room (`SubjectDao`, `SubjectEntity`).
    *   **Remote:** Клиент Retrofit для работы с Google Sheets API.
    *   **Repository:** `SubjectRepositoryImpl` — управляет потоками данных (Single Source of Truth).

3.  **Presentation Layer** (Android Module):
    *   **ViewModel:** Управляет состоянием UI, переживает повороты экрана, общается с UseCases/Repository.
    *   **UI:** Activity/Fragments — отвечают только за отрисовку и обработку нажатий.

---

##  Функционал

*   [x] **Добавление предметов:** Настройка названия, ссылки на таблицу, имени листа и координат ячейки (например, `C5`).
*   [x] **Синхронизация:** Получение актуальных значений из приватных или публичных Google Таблиц через API.
*   [x] **Визуализация:** Круговые индикаторы прогресса на основе текущей и максимальной оценки.
*   [x] **Офлайн режим:** Данные кэшируются в БД Room и доступны без интернета.
*   [x] **Smart Parsing:** Обработка различных форматов чисел (с запятой, с точкой) и текстовых ошибок в ячейках.

---

---

##  Структура проекта

```text
com.android.mygrade
├── data                # Реализация данных (Room, Retrofit)
│   ├── local           # DAO, Database, Entity
│   ├── remote          # API Interface, DTO
│   └── repository      # Реализация репозитория
├── di                  # Hilt модули (Dependency Injection)
├── domain              # Бизнес-логика (Чистая Java)
│   ├── model           # POJO модели
│   └── repository      # Интерфейсы репозиториев
└── presentation        # UI слой
    ├── viewmodel       # ViewModels
    └── ui              # Activities, Adapters
```

---
