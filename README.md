# Parcial Final - Programación N-Capas: Sistema de Pedidos de Restaurante

## Parte 2 (50% Global del Parcial)

- **Unidad:** Seguridad (Autenticación, Autorización, JWT, Roles, Docker, GitHub Actions CI/CD)
- **Uso de Inteligencia Artificial:** Permitido y obligatorio como parte de la evaluación

---

## Introducción

Para el parcial final deberan crear una API para un **sistema de pedidos de restaurante**. No busco que memoricen sintaxis de JWT ni que me repitan un tutorial: quiero ver que son capaces de usar IA como una herramienta de trabajo real, entendiendo y defendiendo por escrito cada decisión de seguridad que tomaron, tal como lo harían en un entorno profesional.

Van a poder apoyarse en ChatGPT, Claude, Copilot o la herramienta que prefieran durante todo el desarrollo. Lo que voy a calificar no es si "les salió", sino si entienden lo que la IA les generó, si lo adaptaron correctamente al caso de negocio que les planteo, y si pueden demostrarlo con evidencia.

---

## 1. Sistema de Pedidos de Restaurante

Desarrollar un proyecto backend de un sistema donde distintos usuarios interactúan con las mesas y los pedidos de una cadena de restaurantes con varias sucursales.

Entidades mínimas:

- **Restaurante/Sucursal** (el sistema maneja más de una sucursal)
- **Mesa** (pertenece a una sucursal, tiene capacidad y estado)
- **Pedido/Orden** (asociado a un cliente, una mesa y una lista de productos)
- **Usuario** (con rol asignado)

Quiero que la arquitectura respete el enfoque N-Capas que hemos visto en clase (Presentación / Lógica de Negocio / Acceso a Datos, como mínimo).

---

## 2. Requisitos Técnicos

### 2.1 Autenticación

- Login con usuario y contraseña, que devuelva un **Access Token (JWT)** y un **Refresh Token**.
- El Access Token debe expirar en un tiempo corto (por ejemplo, 15 minutos) y el Refresh Token en un tiempo mayor (por ejemplo, 7 días).
- Endpoint para renovar el Access Token usando el Refresh Token.

### 2.2 Roles y Autorización

Mínimo estos tres roles, con permisos claramente diferenciados:

| Rol | Permisos |
|---|---|
| Administrador | Acceso total: gestiona restaurantes, mesas, usuarios y pedidos de todas las sucursales |
| Encargado de turno | Gestiona pedidos y mesas, pero **únicamente de la sucursal a la que pertenece** |
| Cliente | Solo puede crear, ver y cancelar sus propios pedidos |

### 2.3 Regla de negocio no trivial (obligatoria)

Además de la autorización básica por rol, deberan implementar **una** de estas reglas (o me propongan una equivalente):

- **Opción A — Invalidación de tokens por cambio de contraseña:** si un usuario cambia su contraseña, todos los tokens emitidos previamente deben quedar inválidos de inmediato, aunque no hayan expirado. Quiero que me expliquen y justifiquen el mecanismo elegido (versión de token, blacklist, etc.).
- **Opción B — Autorización por atributo, no solo por rol:** un Encargado de turno solo puede confirmar, modificar o cancelar pedidos de **su propia sucursal**. Esto no se resuelve solo verificando el rol; requiere lógica adicional que compare la sucursal del usuario autenticado contra la sucursal de la mesa/pedido.
- **Opción C — Expiración forzada por inactividad:** si un Encargado de turno no realiza ninguna petición autenticada durante X minutos, su sesión (refresh token) debe invalidarse automáticamente, incluso si el token aún no expiró.

### 2.4 Docker

- `Dockerfile` funcional para la API.
- `docker-compose.yml` que levante la API junto con su base de datos.
- Se debera levantar el proyecto con (`docker-compose up`).

### 2.5 CI/CD con GitHub Actions

Realizar un pipeline de CI/CD de GitHub Actions, como mínimo:

- Se ejecute automáticamente en cada `push` a la rama principal.
- Compile/construya el proyecto.
- Ejecute las pruebas, si existen.
- Falle si se detecta una vulnerabilidad crítica o un secreto expuesto.

---

## 3. Evidencia de uso de IA

Como se hara uso de IA durante el desarrollo, necesito ver el proceso, no solo el resultado. Estos entregables son tan importantes como el código en sí, y así los voy a calificar.

### 3.1 Repositorio en GitHub

Quiero un historial de commits real e incremental, no un solo commit de "Parcial final". Cada mensaje de commit debe explicar el cambio y, cuando aplique, por qué corrigieron algo que generó la IA. Por ejemplo: `fix: la IA generó autorización solo por rol; se agregó validación de sucursal en el middleware`.

### 3.2 Archivo `PROMPTS.md`

Una bitácora de todos los prompts relevantes que usaron, indicando:

1. Herramienta de IA usada (ChatGPT, Claude, Copilot, etc.).
2. El prompt exacto (o un resumen fiel si fue muy largo).
3. Qué generó la IA (resumen).
4. Qué tuvieron que corregir, rechazar o completar manualmente, y por qué.

### 3.3 Documento de reflexión (`REFLEXION.md`)

Quiero que me respondan con sus propias palabras:

1. ¿Qué partes generó bien la IA sin necesidad de corrección?
2. ¿Qué errores o decisiones incorrectas tomó la IA, especialmente en temas de seguridad?
3. ¿Cómo detectaron esos errores y cómo los corrigieron?
4. Si tuvieran que explicarle a un compañero cómo funciona el mecanismo de autorización por sucursal (o la regla de negocio que eligieron), ¿qué le dirían?

### 3.4 `README.md` del proyecto

- Instrucciones claras para levantar el proyecto con Docker.
- Explicación breve de las capas de la arquitectura.
- Explicación de los roles y de la regla de negocio implementada.

---

## 4. Rubrica

| Componente | Peso |
|---|---|
| Funcionalidad técnica (JWT, roles, Docker, CI/CD operativos) | 30% |
| Implementación correcta de la regla de negocio no trivial | 20% |
| Bitácora de prompts (`PROMPTS.md`) y calidad del proceso documentado | 30% |
| Documento de reflexión (`REFLEXION.md`) | 10% |
| Historial de commits (evidencia de proceso e iteración) | 10% |
| **Total** | **100%** |

---

## 5. Penalizaciones

- Un solo commit, o commits sin mensajes descriptivos.
- Un `PROMPTS.md` genérico, incompleto, o que no coincide con el código que entregaron.
- Código funcional pero sin capacidad de explicar decisiones clave en la parte teórica; eso me indica que no hubo comprensión real del trabajo de la IA.
- La regla de negocio no implementada, o implementada de forma genérica e incorrecta.

---

## 6. Para Cerrar

No quiero medir si son capaces de escribir JWT de memoria. Quiero ver si son capaces de usar IA de forma crítica y responsable en un contexto de seguridad, donde los errores tienen consecuencias reales. Usen la IA, pero verifiquen, cuestionen y entiendan todo lo que les entregue.

---

# Documentación de la implementación

## Cómo levantar el proyecto con Docker

Requisitos: Docker y Docker Compose instalados.

1. Clonar el repositorio y ubicarse en la raíz del proyecto.
2. Crear un archivo `.env` en la raíz (no se versiona, por seguridad) con las siguientes variables:

   ```
   POSTGRES_DB=restaurante
   POSTGRES_USER=restaurante
   POSTGRES_PASSWORD=<una contraseña propia>

   JWT_SECRET=<una cadena aleatoria de al menos 32 caracteres>
   JWT_ACCESS_EXPIRATION_MS=900000
   JWT_REFRESH_EXPIRATION_MS=604800000
   ```

3. Levantar la API junto con la base de datos:

   ```bash
   docker-compose up
   ```

   Esto construye la imagen de la API (`Dockerfile`, build multi-stage, corre como usuario no root),
   levanta un contenedor de PostgreSQL 16 y ejecuta las migraciones de Flyway automáticamente al
   iniciar la API. No hace falta crear tablas a mano.

4. La API queda disponible en `http://localhost:8080`.

Para correr el proyecto sin Docker (`./gradlew bootRun`) hace falta una instancia propia de
PostgreSQL y exportar las mismas variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`,
etc.) apuntando a ella. Los tests (`./gradlew test`) no necesitan ninguna base de datos real: usan
H2 en memoria.

## Arquitectura (N-Capas)

El proyecto sigue una arquitectura de N-Capas dentro del paquete `com.uca.pncparcialfinalrestaurante`:

- **Presentación (`controller`)**: expone los endpoints REST, valida las solicitudes entrantes
  (`@Valid` sobre los DTOs de `dto/request`) y delega toda la lógica de negocio a la capa de
  servicios. No accede directamente a los repositorios.
- **Lógica de negocio (`service`)**: contiene las reglas de negocio, incluida la autorización por
  atributo (sucursal) que exige la regla no trivial del parcial. Traduce entre entidades y los DTOs
  de respuesta (`dto/response`).
- **Acceso a datos (`repository` + `entities`)**: interfaces `JpaRepository` sobre las entidades
  JPA. El esquema de base de datos se versiona con migraciones de Flyway
  (`src/main/resources/db/migration`), no con generación automática de Hibernate.
- **Seguridad (`security` + `configuration`)**: emisión y validación de JWT (`JwtUtil`,
  `JwtAuthFilter`), configuración del filtro de seguridad (`SecurityConfiguration`) y autorización
  por rol a nivel de método (`@PreAuthorize`).
- **Manejo de errores (`exception`)**: excepciones de negocio propias y un `GlobalExceptionHandler`
  centralizado que las traduce a respuestas HTTP consistentes (`ApiError`), incluyendo los
  rechazos que ocurren directamente en el filtro de seguridad.

## Roles y regla de negocio

### Roles

| Rol | Permisos |
|---|---|
| `ADMINISTRADOR` | Acceso total: gestiona sucursales, mesas, productos, usuarios y pedidos de todas las sucursales. |
| `ENCARGADO_TURNO` | Gestiona mesas y pedidos, pero únicamente de la sucursal a la que pertenece. |
| `CLIENTE` | Solo puede crear, ver y cancelar sus propios pedidos. |

### Regla de negocio no trivial: autorización por atributo (Opción B)

Un `ENCARGADO_TURNO` no puede gestionar mesas o pedidos de una sucursal distinta a la suya, aunque
tenga el rol correcto — esto no se puede resolver solo con `@PreAuthorize("hasRole(...)")`, porque
el rol por sí solo no sabe a qué sucursal pertenece cada mesa o pedido.

**Mecanismo:** cada access token incluye el `sucursalId` del usuario autenticado como claim (ver
`JwtUtil`). En cada request, `JwtAuthFilter` reconstruye esa información a partir del token, sin
volver a consultar la base de datos, y la expone a los controladores mediante `CustomUserDetails` /
`AuthenticatedUser`. La capa de servicio (`MesaService`, `PedidoService`) compara explícitamente el
`sucursalId` del usuario autenticado contra el `sucursalId` de la mesa (o de la mesa asociada al
pedido) antes de permitir la operación; si no coinciden, lanza `ForbiddenSucursalException`, que el
`GlobalExceptionHandler` traduce a un `403 Forbidden`.

Un `CLIENTE` tiene una verificación de propiedad análoga (solo puede ver/cancelar sus propios
pedidos), implementada con la excepción estándar de Spring Security `AccessDeniedException` para
diferenciarla explícitamente de la regla de sucursal.
