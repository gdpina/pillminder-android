# PillMinder - Recordatorio de Medicinas

## Funcionalidades implementadas
- [x] **Autenticación:** Implementación de pantallas de Login y Registro de usuarios.
- [x] **Validación:** Control de campos (email, contraseña) y mensajes de error mediante validadores.
- [x] **Navegación:** Flujo seguro entre actividades utilizando `Intent` y flags de navegación.
- [x] **Base de Datos:** Persistencia local de datos utilizando la arquitectura Room.
- [x] **Interfaz:** Diseño basado en Material Design con una interfaz intuitiva y profesional.

## Historial de Commits
![Captura de Commits](https://github.com/gdpina/pillminder-android/blob/master/Captura%20de%20pantalla%202026-06-24%20232621.png?raw=true)

## Captura de Pantalla 1
![App Login](https://github.com/gdpina/pillminder-android/blob/master/Imagen1.jpg?raw=true)

## Captura de Pantalla 2
![App Registro](https://github.com/gdpina/pillminder-android/blob/master/Imagen2.jpg?raw=true)

# PillMinder - Recordatorio de Medicinas 💊

PillMinder es una aplicación nativa de Android diseñada para ayudar a los usuarios a gestionar sus tratamientos médicos, asegurando que tomen sus medicamentos a tiempo mediante un sistema de alarmas y notificaciones locales.

---

## Arquitectura de Datos 🏗️

El proyecto sigue los principios de la arquitectura recomendada de Android para el manejo de datos locales, garantizando una separación de responsabilidades y operaciones asíncronas para no bloquear la interfaz de usuario:

* **UI / Activity:** Las pantallas (como `MainActivity` y `AgregarMedicinaActivity`) se encargan únicamente de renderizar los datos y capturar los eventos del usuario.
* **Room Database:** Actúa como la capa de abstracción sobre SQLite para el almacenamiento local. 
* **DAO (Data Access Object):** Define las operaciones SQL (`@Insert`, `@Query`, `@Update`, `@Delete`). Las consultas a la base de datos se ejecutan en hilos secundarios (Threads) para mantener la fluidez del `RecyclerView`.

---

## Sistema de Notificaciones 🔔

La aplicación es completamente autónoma (Offline First) y no depende de APIs REST externas. Utiliza lógica avanzada de notificaciones locales:

* **WorkManager:** Se utiliza para programar el "Resumen Diario", garantizando que el usuario reciba una alerta por la mañana sobre sus medicinas pendientes, incluso si el dispositivo se reinicia o está en modo reposo (Doze).
* **Notification Channels:** Implementado para cumplir con los estándares de Android 8.0+ (API 26), permitiendo al usuario gestionar la prioridad de las alertas directamente desde los ajustes del sistema.
* **Permisos en Tiempo de Ejecución:** Cumple con las normativas de seguridad de Android 13+ (Tiramisu) solicitando explícitamente el permiso `POST_NOTIFICATIONS` al iniciar la aplicación.

---

## Cómo probar el CRUD (Instrucciones para Desarrolladores) 🧪

Para verificar el correcto funcionamiento de las 4 operaciones fundamentales del CRUD en la aplicación, sigue estos pasos tras compilarla:

1. **Create (Crear):** En la pantalla principal, presiona el botón "AGREGAR MEDICINA". Llena el formulario con un medicamento de prueba (Ej. Paracetamol, 500mg, hora de inicio) y presiona Guardar.
2. **Read (Leer):** Al volver a la pantalla principal, el sistema consultará la base de datos y observarás la aparición inmediata de la medicina recién agregada en la lista principal (`RecyclerView`).
3. **Update (Actualizar):** Haz clic sobre la tarjeta de la medicina en la lista. El formulario se abrirá con los datos ya precargados. Cambia algún valor (Ej. la dosis a 1000mg) y guarda. La lista reflejará el cambio de inmediato.
4. **Delete (Eliminar):** Mantén presionada (Long Click) la tarjeta de la medicina o presiona su botón de eliminar (ícono de basurero). Aparecerá un cuadro de diálogo de confirmación de Material Design. Al aceptar, el registro se borrará de la base de datos y aparecerá un `Snackbar` en la parte inferior confirmando la acción.

---

## Capturas de Pantalla 📸

| Pantalla de Inicio (Read) | Formulario (Create/Update) | Notificación (WorkManager) |
| :---: | :---: | :---: |
| *[Agrega tu link de imagen aquí](https://github.com/gdpina/pillminder-android/blob/master/imagen4.jpg)* | *[Agrega tu link de imagen aquí](https://github.com/gdpina/pillminder-android/blob/master/Imagen5.jpg)* | *[Agrega tu link de imagen aquí](https://github.com/gdpina/pillminder-android/blob/master/Imagen3.jpg)* |

---
*Desarrollado como proyecto de aplicación nativa Android.*de imagen aquí)
