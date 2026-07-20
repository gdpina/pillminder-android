# Manual Técnico — PillMinder (Recordatorio de Medicina) v1.0

## 1. Descripción del sistema
**Problema que resuelve:** La aplicación aborda la falta de adherencia a los tratamientos médicos, ayudando a los pacientes a recordar los horarios exactos de sus tomas y llevar un control de su inventario de pastillas.
**Usuario objetivo:** Pacientes en tratamiento, adultos mayores y cuidadores que necesiten gestionar múltiples medicamentos.
**Alcance del MVP:** Sistema funcional que permite realizar operaciones CRUD (Crear, Leer, Actualizar, Borrar) de medicamentos, programar alarmas locales del sistema y confirmar las tomas mediante notificaciones interactivas, descontando automáticamente el inventario.

## 2. Arquitectura de la aplicación
El proyecto sigue una arquitectura por capas tradicional de Android orientada a eventos (MVC adaptado a Android):
*   **Capa UI (Interfaz de Usuario):** Compuesta por las Activities (`MainActivity`, `AgregarMedicinaActivity`) y archivos XML. Gestiona la interacción con el usuario.
*   **Capa de Lógica / Background:** Utiliza `BroadcastReceiver` (`ConfirmacionReceiver`) para interceptar las alarmas del sistema y un `ExecutorService` (SingleThreadExecutor) para reciclar hilos y procesar eventos sin bloquear la interfaz.
*   **Capa de Datos:** Implementada con Android Room (abstracción de SQLite). Utiliza DAOs (Data Access Objects) para las transacciones directas a la base de datos local.

## 3. Modelo de datos
**Diagrama ER de las entidades principales:**
*   **Tabla `Medicina`**
    *   `id` (Clave Primaria, Autoincremental, Integer)
    *   `nombre` (String, Obligatorio)
    *   `dosis` (Integer)
    *   `inventario` (Integer)
    *   `hora_alarma` (String/Long)
**Descripción:** La base de datos es local y plana para este MVP, centrándose en una única entidad fuerte (`Medicina`) que almacena la configuración de cada recordatorio.

## 4. Tecnologías y librerías
*   **Framework / Entorno:** Android Studio 2023+ (o superior).
*   **Lenguaje:** Java 8+ / 17.
*   **Base de datos:** Android Room Database (v2.5.0+).
*   **Gestión de tareas de fondo:** `AlarmManager` nativo de Android y `ExecutorService` (java.util.concurrent).
*   **Notificaciones:** `NotificationManagerCompat` y `PendingIntent`.

## 5. Instrucciones para compilar
*   **Requisitos:** Android Studio instalado, JDK 8 o superior, Android SDK Mínimo (API 24 - Nougat) y Target SDK (API 33+).
*   **Pasos de compilación:**
    1. Clonar el repositorio desde GitHub: `git clone [URL_DE_TU_REPOSITORIO]`
    2. Abrir Android Studio y seleccionar *Open an existing project*.
    3. Navegar hasta la carpeta clonada y abrir el proyecto.
    4. Esperar a que **Gradle** finalice la sincronización automática de dependencias.
    5. Seleccionar un emulador o dispositivo físico y presionar el botón verde **Run (Shift + F10)**.
*   **Variables de entorno:** No se requieren API keys externas ni `google-services.json` para el MVP, ya que funciona de manera 100% offline y local.

## 6. Estructura del repositorio
*   `/app/src/main/java/com/prograavanzada/recordatoriodemedicina/`: Código fuente Java.
    *   `/data/` (o directorio raíz): Clases de Room (`AppDatabase`, DAOs, Entidades).
    *   `/receivers/`: Clases como `ConfirmacionReceiver`.
    *   `/activities/`: Clases de UI como `MainActivity`, `AgregarMedicinaActivity`.
*   `/app/src/main/res/`: Recursos visuales.
    *   `/layout/`: Archivos XML de diseño de pantallas.
    *   `/drawable/`: Imágenes e íconos (.webp, .xml).
    *   `/values/`: Archivos `strings.xml` y `colors.xml`.

## 7. Historial de versiones
*   **v1.0 — 19 de Julio de 2026** — MVP completo.
    *   Implementación de base de datos Room.
    *   CRUD de medicamentos.
    *   Sistema de alarmas y notificaciones con acciones interactivas.
    *   Optimización de rendimiento (ExecutorService) y control de crashers por validación de nulos.
