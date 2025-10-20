# 📱 Portfolio – CapibaraFamily

![Capibara](https://brandemia.org/sites/default/files/inline/images/logo_youtube.jpg)

An Android application built with **Kotlin** and **Jetpack Compose**, showcasing **Clean Architecture** and **Atomic Design** principles.  
This project demonstrates integration with **ML Kit OCR** and **Gemini API**, continuous delivery pipelines, and best practices in modern Android development.

---

## ✨ Key Features

✅ **Clean Architecture** with `domain`, `data`, and `presentation` layers  
✅ **MVVM** pattern with `ViewModel` + `StateFlow`  
✅ **Atomic Design** (Atoms → Molecules → Organisms → Templates → Pages)  
✅ **Dependency Injection** with **Hilt**  
✅ **Persistence** using **Realm** + **MongoDB Atlas**  
✅ **Modern UI** using **Jetpack Compose** and **Material Design 3**  
✅ **CI/CD** with **GitHub Actions**: Lint, Unit Tests, Instrumented Tests, SonarCloud, Firebase App Distribution, and Play Store deployment  
✅ **OCR integration** using **Google ML Kit**  
✅ **Chatbot integration** using **Gemini API**

---

## 🏗️ Architecture Overview

```text
app/
 ├── di/                 # Hilt modules
 ├── data/               # Repository implementations, mappers, data sources
 ├── domain/             # Use cases and repository interfaces
 ├── presentation/       # ViewModels, UI state, Compose components (Atomic Design)
 ├── core/               # Shared utilities and services (Realm, Gemini, ML Kit)