# AccesibilidApp - Aplicación Móvil de Accesibilidad

## 📱 Descripción

**AccesibilidApp** es una aplicación móvil Android desarrollada en Kotlin con Jetpack Compose, diseñada específicamente para personas con discapacidad sensorial visual. La aplicación facilita la comunicación y mejora la calidad de vida al transformar el dispositivo móvil en una herramienta de integración al mundo real y digital.

## ✨ Funcionalidades Principales

### 🔐 Autenticación

- **Login**: Inicio de sesión con email y contraseña
- **Registro**: Creación de cuenta nueva
- **Recuperar Contraseña**: Recuperación de cuenta mediante email

### 🎯 Herramientas de Accesibilidad

#### ✍️ Escribir

- Campo de texto con tamaño de fuente grande
- Alto contraste para fácil lectura
- Compatible con lectores de pantalla
- Guardado de mensajes

#### 🗣️ Hablar

- **Text-to-Speech (TTS)**: Convierte texto escrito en voz
- **Speech-to-Text**: Dicta por voz y convierte a texto automáticamente
- Ideal para personas con discapacidad visual
- Soporte en español

#### 📍 Buscar Dispositivo

- **Geolocalización GPS**: Obtiene coordenadas del dispositivo
- **Alarma Sonora**: Reproduce sonido fuerte para encontrar el teléfono
- **Vibración**: Activa vibración para localizar el dispositivo cerca
- Útil para personas con discapacidad visual que extravían su teléfono

### ♿ Características de Accesibilidad

- **Modo Oscuro/Claro**: Alto contraste automático
- **Escalado de Fuente**: Aumentar/disminuir tamaño de texto
- **ContentDescriptions**: Compatibilidad completa con lectores de pantalla
- **Barra de Accesibilidad Persistente**: Acceso rápido a funciones desde cualquier pantalla

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **Framework**: Android Studio, Jetpack Compose
- **Base de Datos**: Room (SQLite local)
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Navegación**: Navigation Compose
- **Inyección de Dependencias**: ServiceLocator (sin Hilt)
- **Coroutinas**: Para operaciones asíncronas
- **APIs Android**:
  - TextToSpeech
  - SpeechRecognizer
  - Google Play Services Location
  - MediaPlayer
  - Vibrator

## 📦 Requisitos

- Android Studio Hedgehog o superior
- SDK mínimo: API 31 (Android 12)
- SDK objetivo: API 36
- Kotlin 1.9+
- Google Play Services

## 🚀 Instalación

1. Clonar el repositorio:

```bash
git clone https://github.com/guslaloduoc/accesibilidad-app-android.git

```

2. Abrir el proyecto en Android Studio

3. Sincronizar dependencias Gradle

4. Ejecutar en emulador o dispositivo físico

## 🧪 Testing

El proyecto incluye pruebas completas:

### Tests Unitarios

- `UserDaoTest.kt`: Pruebas de Room con base de datos in-memory
- `AuthRepositoryTest.kt`: Pruebas de lógica de autenticación

### Tests de Integración (Espresso)

- `LoginFlowTest.kt`: Flujo completo de login → home → agregar mensaje

Ejecutar tests:

```bash
./gradlew testDebugUnitTest
./gradlew connectedAndroidTest
```

## 📁 Estructura del Proyecto

```
app/src/main/java/com/example/exp1_s2_gustavo_renjifo/
├── data/
│   └── room/
│       ├── AppDatabase.kt
│       ├── dao/
│       │   ├── UserDao.kt
│       │   └── MessageDao.kt
│       └── entity/
│           ├── User.kt
│           └── Message.kt
├── services/
│   ├── AuthRepository.kt
│   ├── MessageRepository.kt
│   └── ServiceLocator.kt
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt
│   │   ├── HomeViewModel.kt
│   │   ├── EscribirScreen.kt
│   │   ├── HablarScreen.kt
│   │   └── BuscarDispositivoScreen.kt
│   ├── components/
│   │   └── PersistentAccessibilityBar.kt
│   └── theme/
│       └── AppPreferences.kt
├── navigation/
│   ├── AppNavigation.kt
│   └── Screen.kt
├── MainActivity.kt
└── MyApplication.kt
```

## 🔒 Permisos Requeridos

La aplicación solicita los siguientes permisos:

- `ACCESS_FINE_LOCATION`: Para geolocalización del dispositivo
- `ACCESS_COARSE_LOCATION`: Para localización aproximada
- `RECORD_AUDIO`: Para reconocimiento de voz
- `VIBRATE`: Para vibración del dispositivo
- `INTERNET`: Para servicios futuros

## 👥 Uso de la Aplicación

1. **Registro**: Crea una cuenta con email y contraseña
2. **Login**: Inicia sesión con tus credenciales
3. **Home**: Accede al menú principal con 3 funciones principales:
   - **Escribir**: Escribe textos con fuente grande y accesible
   - **Hablar**: Convierte texto a voz o dicta por voz
   - **Buscar Dispositivo**: Localiza tu teléfono con GPS, sonido o vibración
4. **Ajustes de Accesibilidad**: Usa la barra superior para ajustar contraste y tamaño de fuente

## 🎨 Características de Accesibilidad Destacadas

- Todos los botones tienen `contentDescription` para lectores de pantalla
- Tamaños de fuente dinámicos ajustables
- Paleta de colores con alto contraste
- Navegación simple e intuitiva
- Texto grande por defecto (mínimo 16sp, escalable)

## 📄 Licencia

Proyecto académico - DuocUC 2025

## 👨‍💻 Autor

Gustavo Renjifo - Desarrollo de Aplicaciones Móviles

---

**Desarrollado con ❤️ para mejorar la accesibilidad digital**
