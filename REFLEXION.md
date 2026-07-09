# Reflexión

## 1. ¿Qué hizo bien la IA?

La IA me ayudó principalmente a crear la estructura inicial del proyecto. Generó correctamente gran parte de las entidades, repositorios, DTOs, servicios y controladores.

Esto me permitió avanzar más rápido, pero siempre revisé los archivos antes de aceptarlos y realizar los commits.

## 2. ¿Qué errores cometió?

La IA cometió algunos errores relacionados con versiones recientes de las tecnologías utilizadas.

Por ejemplo, generó un `multi-catch` inválido en `JwtUtil` y utilizó importaciones antiguas de Jackson y Spring Boot. También fue necesario revisar cuidadosamente la configuración del archivo `.env`, porque los nombres de las variables no coincidían con los esperados por la aplicación.

Estos errores demostraron que no podía aceptar todo el código automáticamente.

## 3. ¿Cómo los corrigieron?

Detecté la mayoría de los errores ejecutando:

```bash
./gradlew build
```

después de cada fase.

Cuando aparecían errores de compilación, revisaba el mensaje, las dependencias y las clases disponibles en la versión instalada. También comparé la configuración con los archivos reales del proyecto antes de modificar variables de entorno.

La IA ayudó a proponer soluciones, pero yo comprobé que los cambios compilaran y que las pruebas funcionaran.

## 4. ¿Cómo funciona la autorización por sucursal?

Cada encargado tiene una sucursal asignada. Cuando intenta gestionar una mesa o un pedido, el servicio compara su `sucursalId` con la sucursal del recurso.

Si ambas sucursales coinciden, la operación continúa. Si no coinciden, se lanza una excepción de acceso prohibido.

Los administradores pueden acceder a cualquier sucursal, mientras que los clientes solo pueden consultar sus propios pedidos.
