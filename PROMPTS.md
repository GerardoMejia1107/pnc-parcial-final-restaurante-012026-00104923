# Bitácora de prompts

Herramienta utilizada durante el proyecto: **Claude Code** con el modelo **Sonnet 5**.

Cada entrada corresponde a una fase real del desarrollo. Los prompts fueron resumidos conservando las instrucciones principales, las decisiones tomadas y las correcciones realizadas.

---

## 0. Lectura del enunciado y planificación

**Prompt utilizado:**

> Lee el archivo `README.md` para comprender los requisitos. Antes de escribir código, identifica las entidades, roles, endpoints y reglas de negocio.
>
> Utilizaré PostgreSQL y JWT. La regla principal será que un encargado solo pueda gestionar mesas y pedidos de su sucursal.
>
> Divide el trabajo por fases y no realices commits automáticamente, porque quiero revisar y registrar cada cambio manualmente.

**Resultado:**

Se definió un plan con las siguientes etapas:

* Modelo de dominio y repositorios.
* Base de datos y Flyway.
* DTOs, servicios y controladores.
* Seguridad JWT.
* Manejo de excepciones.
* Pruebas.
* Docker, CI/CD y documentación.

Decidí trabajar una fase a la vez, revisar los cambios y realizar personalmente cada commit.

---

## 1. Entidades y repositorios

**Prompt utilizado:**

> Implementa únicamente el modelo de datos y los repositorios.
>
> Las entidades son `Sucursal`, `Mesa`, `Producto`, `Usuario`, `Pedido` y `DetallePedido`.
>
> Mantén las relaciones simples y evita relaciones bidireccionales innecesarias. Revisa también posibles problemas generados por Lombok.

**Resultado:**

Se crearon las entidades, el enum `RoleName` y un `JpaRepository` para cada modelo.

**Corrección realizada:**

En la relación bidireccional `Pedido`–`DetallePedido` se agregaron:

```java
@ToString.Exclude
@EqualsAndHashCode.Exclude
```

Esto evita recursión infinita en los métodos generados por Lombok y posibles errores `StackOverflowError`.

---

## 2. Base de datos con PostgreSQL y Flyway

**Prompt utilizado:**

> Configura PostgreSQL como base de datos principal y utiliza Flyway para controlar el esquema.
>
> Crea la migración inicial según las entidades y utiliza variables de entorno para las credenciales.
>
> Para las pruebas configura H2 en memoria, de forma que no dependan de una instancia real de PostgreSQL.

**Resultado:**

Se agregaron:

* Driver de PostgreSQL.
* Dependencia de Flyway.
* Migración `V1__init_schema.sql`.
* Configuración del datasource mediante variables de entorno.
* Configuración de H2 para pruebas.

**Corrección realizada:**

Se creó:

```text
src/test/resources/application.yaml
```

Este archivo utiliza H2 y desactiva Flyway durante las pruebas, permitiendo ejecutar `./gradlew test` sin PostgreSQL externo.

---

## 3. DTOs de request y response

**Prompt utilizado:**

> Separa las entidades JPA de los datos enviados y recibidos por la API.
>
> Crea DTOs en `dto/request` y `dto/response`, agrega Bean Validation y utiliza `GeneralResponse` como estructura común.
>
> No agregues `@NotNull` a `sucursalId`, porque solo será obligatorio para usuarios con rol `ENCARGADO_TURNO`.

**Resultado:**

Se implementaron DTOs de entrada y salida con validaciones básicas.

La validación condicional de `sucursalId` quedó en la capa de servicio, porque depende también del rol del usuario.

---

## 4. Capa de servicio

**Prompt utilizado:**

> Implementa los servicios de autenticación, usuarios, sucursales, productos, mesas y pedidos.
>
> Un encargado solo puede gestionar recursos de su sucursal, el administrador puede acceder a todas y el cliente únicamente a sus propios pedidos.
>
> Evita utilizar `SecurityContextHolder` directamente en los servicios. Los controladores deben pasar una representación simple del usuario autenticado.
>
> Agrega excepciones específicas para recursos inexistentes, credenciales inválidas, reglas de negocio y accesos a sucursales incorrectas.

**Resultado:**

Se crearon:

* `AuthService`
* `UsuarioService`
* `SucursalService`
* `ProductoService`
* `MesaService`
* `PedidoService`

También se agregaron excepciones como `ResourceNotFoundException`, `InvalidCredentialsException`, `BusinessRuleException` y `ForbiddenSucursalException`.

Para desacoplar los servicios de Spring Security se creó `AuthenticatedUser`.

**Ajuste realizado:**

`JwtUtil` y `PasswordEncoderConfig` se adelantaron porque `AuthService` ya necesitaba generar tokens y cifrar contraseñas.

**Error corregido:**

Se generó inicialmente:

```java
catch (ExpiredJwtException | JwtException | IllegalArgumentException ex)
```

Esto era inválido porque `ExpiredJwtException` hereda de `JwtException`. Se eliminó la excepción redundante.

---

## 5. Controladores

**Prompt utilizado:**

> Implementa los controladores REST para autenticación, usuarios, sucursales, productos, mesas y pedidos.
>
> Utiliza `ResponseEntity<GeneralResponse>`.
>
> Los endpoints protegidos deben obtener al usuario mediante `@AuthenticationPrincipal` y enviarlo a los servicios como `AuthenticatedUser`.
>
> Agrega `@PreAuthorize`, aunque será activado posteriormente en la configuración de seguridad.

**Resultado:**

Se crearon los seis controladores y se adelantó `CustomUserDetails`, necesario para obtener el principal autenticado.

En esta fase las anotaciones `@PreAuthorize` aún no se aplicaban, porque faltaba habilitar `@EnableMethodSecurity`.

---

## 6. Seguridad con JWT

**Prompt utilizado:**

> Implementa un filtro JWT que extraiga el token Bearer, valide que sea de acceso y coloque al usuario dentro del `SecurityContext`.
>
> Configura sesiones stateless, desactiva CSRF y permite acceso público únicamente a registro, login y refresh.
>
> Activa `@EnableMethodSecurity` y evita que un refresh token pueda utilizarse para acceder a endpoints protegidos.

**Resultado:**

Se implementaron:

* `JwtAuthFilter`
* `SecurityConfiguration`
* `SecurityFilterChain`
* Sesiones stateless.
* Validación de tipo de token.
* Seguridad mediante `@PreAuthorize`.

**Corrección realizada:**

Se eliminó `org.springframework.lang.NonNull`, ya que aparece deprecado en Spring Framework 7 y no era necesario para la lógica.

---

## 7. Manejo global de excepciones

**Prompt utilizado:**

> Unifica el formato de los errores mediante `ApiError` y `GlobalExceptionHandler`.
>
> Maneja errores de validación, recursos inexistentes, reglas de negocio, autenticación y autorización.
>
> Los errores 401 y 403 generados por Spring Security también deben utilizar el mismo formato.

**Resultado:**

Se agregaron:

* `ApiError`
* `GlobalExceptionHandler`
* `RestAuthenticationEntryPoint`
* `RestAccessDeniedHandler`

**Error corregido:**

Inicialmente se importó:

```java
com.fasterxml.jackson.databind.ObjectMapper
```

Spring Boot 4.1 utiliza Jackson 3, cuyo paquete correcto es:

```java
tools.jackson.databind.ObjectMapper
```

El cambio se confirmó revisando las dependencias del proyecto.

---

## 8. Pruebas

**Prompt utilizado:**

> Agrega pruebas para JWT, las restricciones por sucursal y la propiedad de pedidos.
>
> También crea una prueba de integración que cubra registro, login, acceso protegido, rechazo de refresh token como Bearer y generación de un nuevo access token.

**Resultado:**

Se crearon:

* `JwtUtilTest`
* `MesaServiceTest`
* `PedidoServiceTest`
* `AuthFlowIntegrationTest`

Las pruebas cubren tokens, roles, sucursales, propiedad de pedidos, autenticación y renovación de sesión.

Se ejecutaron **16 pruebas**, todas correctamente.

**Error corregido:**

`AutoConfigureMockMvc` se importó inicialmente desde la ruta usada en Spring Boot 3.

En Spring Boot 4.1 se encuentra en:

```java
org.springframework.boot.webmvc.test.autoconfigure
```

La ubicación correcta se confirmó inspeccionando el classpath.

---

## 9. Docker y CI/CD

**Prompt utilizado:**

> Crea un `Dockerfile` multi-stage con Java 21 y ejecución mediante usuario sin privilegios.
>
> Agrega `docker-compose.yml` con la API y PostgreSQL 16 Alpine, incluyendo healthcheck.
>
> Utiliza variables de entorno para la conexión y el secreto JWT.
>
> Crea un pipeline que compile, ejecute pruebas, revise secretos y escanee vulnerabilidades críticas de la imagen.

**Resultado:**

Se agregaron:

* `Dockerfile`
* `.dockerignore`
* `docker-compose.yml`
* `.github/workflows/ci.yml`

El pipeline incluye build, pruebas, Gitleaks y Trivy.

**Decisión tomada:**

El `.env` existente utilizaba nombres de variables diferentes y una conexión mediante `localhost`, que no funcionaría dentro del contenedor.

Se decidió que Docker Compose administraría PostgreSQL y se ajustaron las variables necesarias.

**Limitación:**

No fue posible ejecutar Docker dentro del entorno utilizado. Sin embargo, se verificó que:

```bash
./gradlew bootJar
```

generara un único archivo JAR compatible con el `COPY` del Dockerfile.

---

## 10. Documentación final

**Prompt utilizado:**

> Actualiza el `README.md` con instrucciones de ejecución, arquitectura, roles y regla de negocio.
>
> Crea `PROMPTS.md` para registrar las instrucciones, decisiones y correcciones realizadas.
>
> En `REFLEXION.md` deja únicamente las preguntas y algunas ideas guía, porque las respuestas deben escribirse personalmente.

**Resultado:**

Se actualizaron o crearon:

* `README.md`
* `PROMPTS.md`
* `REFLEXION.md`

El `README.md` explica la ejecución con Docker, variables de entorno, arquitectura, roles, pruebas y reglas de acceso.

`PROMPTS.md` documenta el uso de Claude Code como herramienta de apoyo durante el desarrollo.

Las respuestas de `REFLEXION.md` quedaron pendientes para ser redactadas personalmente.
