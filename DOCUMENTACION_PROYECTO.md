# DOCUMENTACIÓN DEL PROYECTO
## AccesibilidApp - Aplicación Básica de Accesibilidad

**Estudiante:** Gustavo Renjifo  
**Asignatura:** Desarrollo de Aplicaciones Móviles  
**Actividad:** Exp1_S2 - Iniciando la creación de una aplicación móvil con Android Studio  
**Fecha:** Agosto 2025

---

## 1. ALCANCE DEL PROYECTO

### Objetivo Principal
Desarrollar una aplicación móvil básica con Android Studio y Jetpack Compose, implementando exactamente los requerimientos mínimos: 3 pantallas con componentes UI específicos y array de 5 usuarios.

### Funcionalidades Implementadas
1. **Pantalla Login:**
   - Input field para email
   - Input field para contraseña con toggle de visibilidad
   - Botón de inicio de sesión
   - Enlaces de navegación (vínculos)
   - Textos descriptivos

2. **Pantalla Registro:**
   - Input fields (nombre, email, contraseña)
   - Radio buttons (3 opciones de tipo de asistencia)
   - Checkboxes (términos y notificaciones)  
   - Botón de registro
   - Textos explicativos

3. **Pantalla Recuperar Contraseña:**
   - Input field para email
   - Botón de envío
   - Texto de confirmación

4. **Array de 5 Usuarios:**
   - Datos almacenados en UserRepository
   - Funcionalidad de registro que agrega al array

### Componentes UI Implementados (Requerimiento)
- ✅ **Input fields:** Campos de texto básicos
- ✅ **Buttons:** Botones principales de acción
- ✅ **Vínculos:** Enlaces clickeables de navegación
- ✅ **Textos:** Títulos y textos descriptivos
- ✅ **Radio buttons:** Selección única de opciones
- ✅ **Checkboxes:** Selecciones múltiples

---

## 2. MOCKUPS Y DISEÑO BÁSICO

### Login Screen
- Título "AccesibilidApp"
- Campo Email
- Campo Contraseña (con icono de visibilidad)
- Botón "INICIAR SESIÓN"
- Link "¿No tienes cuenta? Regístrate"
- Link "¿Olvidaste tu contraseña?"

### Register Screen  
- TopAppBar con botón atrás
- Título "Crear Cuenta"
- Campos: Nombre, Email, Contraseña
- Radio buttons: Lector de pantalla, Alto contraste, Comandos de voz
- Checkboxes: Términos, Notificaciones
- Botón "REGISTRARSE"

### Forgot Password Screen
- TopAppBar con botón atrás
- Título "Recuperar Contraseña" 
- Campo Email
- Botón "ENVIAR INSTRUCCIONES"
- Mensaje de confirmación

---

## 3. ESTRUCTURA TÉCNICA BÁSICA

### Navegación Simple
```
Login ↔ Register
Login ↔ ForgotPassword
```

### Archivos Principales
```
MainActivity.kt - Actividad principal
Screen.kt - Definición de rutas
AppNavigation.kt - Navegación básica
LoginScreen.kt - Pantalla de login
RegisterScreen.kt - Pantalla de registro  
ForgotPasswordScreen.kt - Pantalla recuperar contraseña
User.kt - Modelo de datos
UserRepository.kt - Array de usuarios
```

### Usuarios Predefinidos (5)
1. María Silva - maria.silva@email.com / maria123
2. Carlos Méndez - carlos.mendez@email.com / carlos456  
3. Ana Rodríguez - ana.rodriguez@email.com / ana789
4. José Torres - jose.torres@email.com / jose321
5. Lucía Ramírez - lucia.ramirez@email.com / lucia654

---

## 4. REQUERIMIENTOS CUMPLIDOS

### ✅ Requerimientos Técnicos
- **Android Studio Framework** ✓
- **Material Design + Jetpack Compose** ✓
- **3 Views requeridas** ✓
- **Array de 5 usuarios** ✓
- **Componentes UI especificados** ✓

### ✅ Componentes UI Verificados
- **Input fields** - Campos de texto en todas las pantallas ✓
- **Buttons** - Botones de acción principales ✓
- **Vínculos** - Enlaces de navegación ✓
- **Textos** - Títulos y descripciones ✓
- **Radio buttons** - Opciones de tipo de asistencia ✓
- **Checkboxes** - Términos y notificaciones ✓

---

## 5. FUNCIONALIDADES BÁSICAS

### Login
- Validación contra array de usuarios
- Mensajes de error/éxito simples
- Navegación a registro y recuperar contraseña

### Registro  
- Agregar usuario al array
- Validación básica de campos completados
- Mensaje de confirmación

### Recuperar Contraseña
- Verificación de email en array
- Mensaje de confirmación simulado
- Navegación de regreso

---

## 6. CONCLUSIÓN

Esta es una implementación **BÁSICA** que cumple exactamente con los requerimientos especificados:

- ✅ 3 pantallas (Login, Registro, Recuperar)
- ✅ Todos los componentes UI solicitados
- ✅ Array de 5 usuarios funcionando
- ✅ Navegación básica entre pantallas
- ✅ Material Design y Jetpack Compose
- ✅ Código simple y evaluable

**Sin funcionalidades extras, sin complicaciones, solo lo esencial solicitado.**

---

**Documentación para evaluación inicial - Exp1_S2**  
**Desarrollo de Aplicaciones Móviles - DuocUC**