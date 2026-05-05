# Synq

Synq is a modern, fast, and reliable Android chat application built with Kotlin and Jetpack Compose. It prioritizes offline-first functionality, real-time message synchronization, and a smooth, minimalist user experience.

## Architecture

Synq is built using a strict, production-grade **Clean Architecture** combined with **MVVM**. The goal is separation of concerns, scalability, and robust testing capabilities.

### Layers:
1. **Presentation (UI):** Jetpack Compose, ViewModels (Hilt injected), StateFlows. Strict "UI only renders state" pattern. No business logic in composables.
2. **Domain:** Use Cases, Domain Models, and Repository Interfaces. This represents the core business rules of the chat application.
3. **Data:** Repository Implementations, Room Database (Local Source of Truth), Retrofit/OkHttp (Remote API), and Mappers.
4. **Core:** Shared utilities, Network Interceptors, DI Modules, Result Wrappers, and Theme configuration.

## Features
*   **Phone Number OTP Authentication:** Secure token-based auth flow.
*   **Real-Time Messaging:** Instant message delivery and sync via WebSockets.
*   **Offline Support:** Full local caching.
*   **Dark Mode First:** Modern, deep blue and teal UI optimized for OLED screens and low-light usage.
