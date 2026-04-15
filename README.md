# VPN Guard — Android Demo App

Тестовое задание: демо VPN-приложение на Jetpack Compose + MVVM + Clean Architecture.

---

## Как запустить проект

**Требования:**
- Android Studio Hedgehog (2023.1.1) или новее
- JDK 17+
- Android SDK 26+

**Шаги:**
1. Клонировать репозиторий:
   ```bash
   git clone https://github.com/YOUR_USERNAME/VPNTestAndroid.git
   cd VPNTestAndroid
   ```
2. Открыть проект в Android Studio (`File → Open`)
3. Дождаться синхронизации Gradle
4. Запустить на эмуляторе или устройстве (`Run → Run 'app'`)

> Интернет-подключение необходимо для загрузки списка стран. При первом запуске данные кэшируются в Room — последующие запуски работают офлайн.

---

## Архитектура

```
UI (Compose) ← ViewModel ← UseCase ← Repository ← DataSource (API + Room)
```

- **MVVM** — `MainViewModel` хранит весь UI-стейт в `StateFlow<MainUiState>`
- **Clean Architecture** — слои изолированы: `domain` не знает об `android.*`, DTOs не проникают в UI
- **Hilt** — DI через `@HiltViewModel`, `@Singleton`, `@Binds`/`@Provides`
- **Retrofit + OkHttp** — сетевой слой, `https://restcountries.com/v3.1/all`
- **Room** — кэширование списка стран, стратегия `REPLACE`
- **Mapper pattern** — `CountryDto → CountryEntity → Country` (domain model)

---

## Что было исправлено в UI

| Проблема оригинала | Решение |
|---|---|
| Кнопка Connect занимала непропорционально много места | Круглая кнопка 128dp с визуальными слоями, не перегружает экран |
| Нет визуального различия между состояниями | Анимированные цвета (cyan/orange/green), пульсирующие кольца, rotating progress indicator |
| Плоский, безжизненный интерфейс | Radial gradient glow на фоне реагирует на статус подключения |
| Список стран без состояний | Loading / Error с Retry / Empty — все три состояния обработаны |
| Нет иерархии элементов | Status badge в хедере, крупный статус-лейбл, subtitle с пояснением |

---

## UX-решения

**Цветовая система:**
- `#00D4FF` (Cyan) — нейтральный, "готов к подключению"
- `#00E676` (Green) — Connected, безопасно
- `#FFB300` (Amber) — Connecting, переходное состояние
- `#546E7A` (Slate) — Disconnected, неактивный

Цвета намеренно насыщенные на тёмном фоне — это стандарт для security/VPN приложений (схожий подход у ProtonVPN, Mullvad).

**Анимации:**
- Пульсирующие кольца во время `CONNECTING` (InfiniteTransition)
- Вращающийся CircularProgressIndicator поверх кнопки
- `animateColorAsState` с `tween(600)` — плавные переходы между статусами
- Ambient glow фона меняется вместе со статусом

**Server Picker:**
- ModalBottomSheet — нативный паттерн Android, не перекрывает весь экран
- Секция "Recommended" с базовыми серверами отделена от полного списка
- Флаги стран через Coil + flagcdn.com
- Активный сервер подсвечен cyan-бордером с иконкой checkmark

**Адаптивность:**
- `windowInsetsPadding(WindowInsets.statusBars)` — корректная работа с edge-to-edge
- Все размеры в `dp`, типографика через `MaterialTheme.typography`
- LazyColumn в шторке — корректно работает со списком из 250+ стран

---

## Стек

| | |
|---|---|
| UI | Jetpack Compose, Material3 |
| DI | Hilt |
| Network | Retrofit 2, OkHttp 4, Gson |
| DB | Room |
| Images | Coil |
| Async | Kotlin Coroutines, StateFlow |
| Architecture | MVVM, Clean Architecture |
