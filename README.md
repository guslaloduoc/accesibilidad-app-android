# AccesibilidApp - Aplicación de Accesibilidad Móvil

Una aplicación Android básica desarrollada con **Jetpack Compose** y **Material Design 3** para la evaluación inicial del curso de Desarrollo de Aplicaciones Móviles en DuocUC.

## Características Principales

### **Interfaz Visual Atractiva**

- **Fondo degradado pastel** en pantalla de login (azul → rosa → verde)
- **Card semi-transparente** con elevación y sombra
- **Diseño Material Design 3** moderno y limpio
- **Colores armoniosos** y accesibles

### **Sistema de Acceso Simplificado**

- **Login directo** - Sin validación de credenciales
- **Acceso inmediato** al presionar "INICIAR SESIÓN"
- **Pantalla de éxito** con mensaje claro
- **Lista completa** de los 5 usuarios registrados

### **Pantallas Implementadas**

1. **Login** - Interfaz principal con degradado pastel
2. **Registro** - Formulario completo con todos los componentes UI
3. **Recuperar Contraseña** - Formulario simple de recuperación
4. **Pantalla de Éxito** - Muestra acceso exitoso y lista de usuarios

## **Componentes UI Implementados**

### **Todos los Requerimientos Cumplidos:**

- **Input fields** - Campos de texto con labels
- **Buttons** - Botones de acción estilizados
- **Vínculos** - Enlaces de navegación coloridos
- **Textos** - Títulos y descripciones
- **Radio buttons** - Selección única de opciones
- **Checkboxes** - Casillas de verificación múltiple
- **Cards** - Contenedores con elevación
- **Navigation** - Navegación fluida entre pantallas

## 👥 **Usuarios Predefinidos (Array de 5)**

La aplicación incluye 5 usuarios precargados que se muestran en la pantalla de éxito:

1. **María Silva**

   - Email: maria.silva@email.com
   - Usuario: maria_silva
   - **Contraseña: maria123**

2. **Carlos Méndez**

   - Email: carlos.mendez@email.com
   - Usuario: carlos_mendez
   - **Contraseña: carlos456**

3. **Ana Rodríguez**

   - Email: ana.rodriguez@email.com
   - Usuario: ana_rodriguez
   - **Contraseña: ana789**
4. **José Torres**

   - Email: jose.torres@email.com
   - Usuario: jose_torres
   - **Contraseña: jose321**

5. **Lucía Ramírez**
   - Email: lucia.ramirez@email.com
   - Usuario: lucia_ramirez
   - **Contraseña: lucia654**

## **Cómo Usar la Aplicación**

### **Flujo Principal:**

```
Login (con fondo degradado)
    ↓ [Presionar "INICIAR SESIÓN"]
Pantalla de Éxito (muestra los 5 usuarios)
```

### **Navegación Adicional:**

- **Desde Login:** Ir a Registro o Recuperar Contraseña
- **Registro:** Llenar formulario y agregar nuevo usuario
- **Recuperar:** Ingresar email y recibir confirmación

## **Tecnologías Utilizadas**

- **Android Studio** - Framework principal
- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - UI moderna y declarativa
- **Material Design 3** - Sistema de diseño
- **Navigation Compose** - Navegación entre pantallas
- **Gradle Version Catalogs** - Gestión de dependencias

## **Estructura del Proyecto**

```
app/src/main/java/com/example/exp1_s2_gustavo_renjifo/
├── data/
│   ├── User.kt                 # Modelo de datos
│   └── UserRepository.kt       # Array de 5 usuarios
├── navigation/
│   ├── Screen.kt               # Rutas de navegación
│   └── AppNavigation.kt        # Configuración de navegación
├── ui/
│   ├── screens/
│   │   ├── LoginScreen.kt      # Login con degradado pastel
│   │   ├── RegisterScreen.kt   # Formulario de registro
│   │   ├── ForgotPasswordScreen.kt  # Recuperar contraseña
│   │   └── SuccessScreen.kt    # Pantalla de éxito con usuarios
│   └── theme/                  # Tema Material 3
└── MainActivity.kt             # Actividad principal
```

## **Requerimientos Cumplidos**

### **Técnicos:**

- Framework Android Studio ✓
- Material Design + Jetpack Compose ✓
- 3 Views principales ✓
- Array de 5 usuarios ✓
- Navegación funcional ✓

### **Componentes UI:**

- Input fields ✓
- Buttons ✓
- Vínculos ✓
- Textos ✓
- Radio buttons ✓
- Checkboxes ✓

### **Características Extra:**

- Fondo degradado pastel ✓
- Interfaz atractiva y moderna ✓
- Acceso directo simplificado ✓
- Pantalla de éxito con lista de usuarios ✓

## **Instalación y Ejecución**

1. **Clonar el repositorio:**

   ```bash
   git clone [URL_DEL_REPOSITORIO]
   ```

2. **Abrir en Android Studio:**

   - File → Open → Seleccionar la carpeta del proyecto

3. **Sincronizar dependencias:**

   - Android Studio sincronizará automáticamente

4. **Ejecutar la aplicación:**

   ```bash
   ./gradlew assembleDebug
   # o usar el botón Run en Android Studio
   ```

5. **Instalar en dispositivo/emulador:**
   ```bash
   ./gradlew installDebug
   ```

## 📸 **Vista Previa**

La aplicación presenta:

- **Login** con hermoso fondo degradado pastel
- **Formularios** limpios y bien organizados
- **Navegación** fluida y intuitiva
- **Lista de usuarios** clara y legible
- **Diseño responsivo** para diferentes tamaños de pantalla

## **Documentación Adicional**

- `DOCUMENTACION_PROYECTO.md` - Documentación completa del proyecto

---

**Desarrollado por:** Gustavo Renjifo  
**Curso:** Desarrollo de Aplicaciones Móviles - DuocUC  
**Actividad:** Exp1_S2 - Evaluación Inicial  
**Año:** 2025

---

### **Características Destacadas:**

- **Fondo degradado pastel único**
- **Interfaz moderna y atractiva**
- **Acceso directo simplificado**
- **Totalmente funcional y responsive**
- **Cumple 100% los requerimientos**
