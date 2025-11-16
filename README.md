<h1>
<p style="text-align: center">
<a href="https://github.com/GnomeShift/RateCourse" target="_blank" rel="noopener noreferrer">RateCourse</a>
</p>
</h1>

<p style="text-align: center">
  <a href="README.md">🇷🇺 Русский</a>
</p>

## 🚀Быстрая навигация
* [Обзор](#обзор)
    * [Функции](#функции)
* [Установка и конфигурация](#установка-и-конфигурация)
    * [API](#api)

# 🌐Обзор
**RateCourse** - это минимальный пример платформы рекомендаций курсов с микросервисной архитектурой.

## ⚡Функции
* Генерация персональных рекомендаций.
* Управление курсами.
* Управление рейтингами.
* Управление рекомендациями.
* REST API.
* Поддержка Postgres.

# ⚙️Установка и конфигурация
#### 1️⃣ Клонируйте репозиторий:
```bash
git clone https://github.com/GnomeShift/RateCourse
```

#### 2️⃣ Перейдите в папку:
```bash
cd ratecourse
```

#### 3️⃣ Сгенерируйте рандомный JWT-секрет:
```bash
sed -i "s/^JWT_SECRET=.*/JWT_SECRET=$(openssl rand -hex 64)/" .env.production
```

#### 4️⃣ Сгенерируйте пароль для Postgres:
```bash
pw=$(openssl rand -hex 24) && sed -i "s/^DB_PASSWORD=.*/POSTGRES_PASSWORD=$pw/" .env.production
```

#### 5️⃣ Запустите контейнеры:
```bash
docker-compose --env-file .env.production -f docker-compose-production.yml -p ratecourse up -d && docker-compose -f docker-compose-caddy.yml -p ratecourse up -d
```

# 📡API
**URL для отправки запросов:**
> http://localhost:80/{api_endpoint}

В таблице ниже приведены доступные API-эндпоинты.

| API-Endpoint                               | Метод  | Описание                                            | Тело запроса                                                                                                                                      |
|--------------------------------------------|--------|-----------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| {URL}**/api/users/register**               | POST   | Регистрация пользователя                            | `{ username: "USERNAME", email: "mail@example.com", password: "PASSWORD", firstName: "FIRST_NAME, lastName: "LAST_NAME" }`                        |
| {URL}**/api/users/login**                  | POST   | Авторизация пользователя                            | `{ username: "USERNAME", password: "PASSWORD" }`                                                                                                  |
| {URL}**/api/users/profile**                | GET    | Просмотр профиля пользователя                       | -                                                                                                                                                 |
| {URL}**/api/courses**                      | GET    | Просмотр всех доступных курсов                      | -                                                                                                                                                 |
| {URL}**/api/courses/{id}**                 | GET    | Просмотр курса по его ID                            | -                                                                                                                                                 |
| {URL}**/api/courses/search?query={query}** | GET    | Поиск курса по его названию                         | -                                                                                                                                                 |
| {URL}**/api/courses/category/{category}**  | GET    | Поиск курсов по их категории                        | -                                                                                                                                                 |
| {URL}**/api/courses**                      | POST   | Создание курса                                      | `{ title: "TITLE", desecription: "DESCRIPTION", category: "CATEGORY", duration: 12, level: "BEGINNER/INTERMEDIATE/ADVANCED", price: 100}`         |
| {URL}**/api/courses/{id}**                 | PATCH  | Изменение курса по его ID                           | `{ title: "TITLE UPDATED", desecription: "DESCRIPTION", category: "CATEGORY", duration: 12, level: "BEGINNER/INTERMEDIATE/ADVANCED", price: 100}` |
| {URL}**/api/courses/{id}**                 | DELETE | Удаление курса по его ID                            | -                                                                                                                                                 |
| {URL}**/api/ratings**                      | POST   | Создание рейтинга                                   | `{ userId: 1, courseId: 1, rating: 5, comment: "COMMENT" }`                                                                                       |
| {URL}**/api/ratings/{id}**                 | PATCH  | Обновление рейтинга курса по его ID                 | `{ userId: 1, courseId: 1, rating: 4, comment: "COMMENT UPDATED" }`                                                                               |
| {URL}**/api/ratings/course/{id}**          | GET    | Просмотр рейтинга курса по его ID                   | -                                                                                                                                                 |
| {URL}**/api/ratings/{id}/stats**           | GET    | Просмотр статистики курса по его ID                 | -                                                                                                                                                 |
| {URL}**/api/ratings/user/{id}**            | GET    | Просмотр рейтингов пользователя по его ID           | -                                                                                                                                                 |
| {URL}**/api/ratings/{id}**                 | DELETE | Удаление рейтинга курса по его ID                   | -                                                                                                                                                 |
| {URL}**/api/recommendations/{id}**         | GET    | Просмотр рекомендаций пользователя по его ID        | -                                                                                                                                                 |
| {URL}**/api/recommendations/{id}/top**     | GET    | Просмотр лучших рекомендаций пользователя по его ID | -                                                                                                                                                 |
