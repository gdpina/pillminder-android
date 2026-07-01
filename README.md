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

PillMinder - Recordatorio de Medicinas 💊PillMinder es una aplicación nativa de Android diseñada para ayudar a los usuarios a gestionar sus tratamientos médicos, asegurando que tomen sus medicamentos a tiempo mediante un sistema de alarmas y notificaciones locales.Arquitectura de Datos 🏗️El proyecto sigue los principios de la arquitectura recomendada de Android para el manejo de datos locales, garantizando una separación de responsabilidades y operaciones asíncronas para no bloquear la interfaz de usuario:UI / Activity: Las pantallas (como MainActivity y AgregarMedicinaActivity) se encargan únicamente de renderizar los datos y capturar los eventos del usuario.Room Database: Actúa como la capa de abstracción sobre SQLite.DAO (Data Access Object): Define las operaciones SQL (@Insert, @Query, @Delete). Las consultas a la base de datos se ejecutan en hilos secundarios (Threads) para mantener la fluidez del RecyclerView.Sistema de Notificaciones 🔔La aplicación es completamente autónoma (Offline First) y no depende de APIs REST externas. Utiliza lógica avanzada de notificaciones locales:WorkManager: Se utiliza para programar el "Resumen Diario", garantizando que el usuario reciba una alerta por la mañana sobre sus medicinas pendientes, incluso si el dispositivo se reinicia.Notification Channels: Implementado para cumplir con los estándares de Android 8.0+, permitiendo al usuario gestionar la prioridad de las alertas.Permisos en Tiempo de Ejecución: Cumple con las normativas de Android 13+ (Tiramisu) solicitando explícitamente el permiso POST_NOTIFICATIONS.Cómo probar el CRUD (Instrucciones para Desarrolladores) 🧪Para verificar el correcto funcionamiento de las 4 operaciones fundamentales, sigue estos pasos tras compilar la app:Create (Crear): En la pantalla principal, presiona el botón "AGREGAR MEDICINA". Llena el formulario con un medicamento de prueba (Ej. Paracetamol, 500mg) y presiona Guardar.Read (Leer): Al volver a la pantalla principal, observarás un estado de carga (Loading) seguido por la aparición inmediata de la medicina recién agregada en el RecyclerView.Update (Actualizar): Haz clic sobre la tarjeta de la medicina en la lista. El formulario se abrirá con los datos precargados. Cambia la dosis (Ej. a 1000mg) y guarda. La lista reflejará el cambio.Delete (Eliminar): Mantén presionada (Long Click) la tarjeta de la medicina o presiona el botón de eliminar. Aparecerá un cuadro de diálogo de confirmación. Al aceptar, el registro se borrará de la base de datos y aparecerá un Snackbar de confirmación en la parte inferior.Capturas de Pantalla 📸Pantalla de Inicio (Read)Formulario (Create/Update)Notificación (WorkManager)(Agrega tu link de imagen aquí)(Agrega tu link de imagen aquí)(Agrega tu link de imagen aquí)
