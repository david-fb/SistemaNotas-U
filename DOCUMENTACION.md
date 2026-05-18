# Documentación Técnica — Sistema de Notas

---

## ¿Qué es una API REST?

Una **API REST** es un programa que escucha peticiones HTTP (las mismas que usa un navegador) y responde con datos en formato **JSON**.

El frontend (HTML/JS) le habla al backend así:

```
Frontend                               Backend (Java)
   |                                        |
   |  POST /api/auth/login                  |
   |  { "correo": "...", "pass": "..." }    |
   | ------------------------------------> |
   |                                        |  valida, busca en BD
   |  { "token": "abc123", "rol": "admin" } |
   | <------------------------------------ |
```

El backend vive en `http://localhost:8080`.
El frontend corre en `http://localhost:5500` (Live Server).

---

## Métodos HTTP

| Método | Para qué se usa | Ejemplo |
|--------|----------------|---------|
| **GET** | Leer o consultar datos. No modifica nada. | Listar usuarios, ver mis notas |
| **POST** | Crear algo nuevo. El body trae los datos. | Crear usuario, registrar nota |
| **PUT** | Editar algo existente. El body trae los nuevos datos. | Editar nombre de un usuario |
| **DELETE** | Eliminar o desactivar algo. | Desactivar un usuario |

---

## Códigos de respuesta HTTP

| Código | Nombre | Cuándo usarlo |
|--------|--------|---------------|
| **200** | OK | Todo salió bien (GET, PUT, DELETE exitosos) |
| **201** | Created | Se creó algo nuevo (POST exitoso) |
| **400** | Bad Request | El cliente mandó datos inválidos |
| **401** | Unauthorized | No hay token o el token es inválido |
| **403** | Forbidden | El rol no tiene permiso para esa acción |
| **404** | Not Found | El recurso no existe |
| **500** | Internal Server Error | Error inesperado en el servidor |

> El `AuthMiddleware` maneja el **401** automáticamente.
> El **400** se lanza desde el Service con `throw new IllegalArgumentException("mensaje")`.
> Solo necesitas manejar **403**, **404** y **500** en el controller.

---

## Headers de una petición

```
Content-Type: application/json      ← indica que el body es JSON (POST y PUT)
Authorization: Bearer <token>       ← el token que devuelve el login
```

En Postman ya están configurados en la colección.
En el frontend, `api.js` los envía automáticamente en cada request.

---

## Flujo de una petición HTTP

```
Postman / Frontend
      ↓  POST /api/cursos  { "nombre": "...", ... }

Application.java      → registra la ruta con el controller
      ↓
AuthMiddleware.java    → valida que el token sea válido
      ↓                   si no, responde 401 automáticamente
XxxController.java    → decide qué hacer según método (GET/POST/PUT/DELETE)
      ↓
XxxService.java       → valida las reglas de negocio
      ↓
XxxRepository.java    → ejecuta el SQL y retorna el objeto Java
      ↓
XxxController.java    → convierte el objeto a JSON y responde
```

---

## Estructura del proyecto

```
SistemaNotas-U/
├── backend/SistemaNotas-Api/src/
│   ├── config/       → Conexión a la base de datos
│   ├── model/        → Clases que representan las tablas (ya creadas)
│   ├── repository/   → Consultas SQL directas a la BD
│   ├── service/      → Reglas de negocio y validaciones
│   ├── controller/   → Recibe peticiones HTTP y envía respuestas JSON
│   ├── middleware/   → Valida el token antes de llegar al controller
│   ├── util/         → Herramientas compartidas
│   └── notas/        → Application.java — punto de entrada del servidor
├── db/
│   ├── schema.sql    → Crea las tablas
│   └── seed.sql      → Datos de prueba
└── postman/
    └── SistemaNotas.postman_collection.json
```

---

## Modelos disponibles

Todos tienen constructor completo, getters/setters y `toMap()`.

| Modelo | Campos |
|--------|--------|
| `Usuario` | id, nombre, correo, password, rol, activo |
| `Semestre` | id, nombre, fechaInicio, fechaFin |
| `Curso` | id, nombre, codigo, profesorId, semestreId |
| `Matricula` | id, cursoId, estudianteId, fecha, estado |
| `Corte` | id, cursoId, porcentaje |
| `Nota` | id, estudianteId, corteId, valor |

---

## Herramientas disponibles — HttpHelper

```java
HttpHelper.getMethod(exchange)               // "GET", "POST", "PUT", "DELETE"
HttpHelper.getUsuario(exchange)              // Usuario autenticado (id, nombre, rol...)
HttpHelper.readJsonBody(exchange)            // Lee el body JSON → Map<String, Object>
HttpHelper.getPathParam(exchange, 3)         // /api/cursos/5 → "5"
HttpHelper.getQueryParam(exchange, "nombre") // /api/cursos?nombre=POO → "POO"
HttpHelper.sendJson(exchange, 200, map)      // Responde con un objeto JSON
HttpHelper.sendJsonArray(exchange, 200, str) // Responde con un array JSON
HttpHelper.sendError(exchange, 400, "msg")   // Responde con error
HttpHelper.handleCors(exchange)              // Para peticiones OPTIONS (no tocar)
```

---

## Patrón de código — Repository

Archivo: `repository/XxxRepository.java`

```java
package repository;

import config.DatabaseConfig;
import model.Curso;
import java.sql.*;
import java.util.*;

public class CursoRepository {

    // Convierte una fila del ResultSet en un objeto
    // Los nombres de columna deben coincidir exactamente con schema.sql
    private Curso mapRow(ResultSet rs) throws SQLException {
        return new Curso(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("codigo"),
            rs.getInt("profesor_id"),
            rs.getInt("semestre_id")
        );
    }

    public List<Curso> findAll() {
        String sql = "SELECT * FROM curso ORDER BY id";
        List<Curso> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar cursos", e);
        }
    }

    public Curso findById(int id) {
        String sql = "SELECT * FROM curso WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar curso", e);
        }
    }

    public Curso save(Curso curso) {
        // RETURNING * hace que PostgreSQL devuelva la fila creada con el id generado
        String sql = "INSERT INTO curso (nombre, codigo, profesor_id, semestre_id) " +
                     "VALUES (?, ?, ?, ?) RETURNING *";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, curso.getNombre());
            stmt.setString(2, curso.getCodigo());
            stmt.setInt(3, curso.getProfesorId());
            stmt.setInt(4, curso.getSemestreId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear curso", e);
        }
    }
}
```

---

## Patrón de código — Service

```java
package service;

import model.Curso;
import repository.CursoRepository;
import java.util.List;

public class CursoService {

    private final CursoRepository cursoRepository = new CursoRepository();

    public List<Curso> getAll() {
        return cursoRepository.findAll();
    }

    public Curso create(String nombre, String codigo, int profesorId, int semestreId) {
        // Lanzar IllegalArgumentException cuando algo no sea válido
        // El controller lo captura automáticamente y responde 400
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
        if (codigo == null || codigo.isBlank())
            throw new IllegalArgumentException("El código es obligatorio");

        return cursoRepository.save(new Curso(0, nombre, codigo, profesorId, semestreId));
    }
}
```

---

## Patrón de código — Controller

```java
package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Curso;
import model.Usuario;
import service.CursoService;
import util.HttpHelper;
import util.JsonUtil;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CursoController implements HttpHandler {

    private final CursoService cursoService = new CursoService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpHelper.getMethod(exchange).equals("OPTIONS")) {
            HttpHelper.handleCors(exchange);
            return;
        }

        String path    = exchange.getRequestURI().getPath();
        String method  = HttpHelper.getMethod(exchange);
        Usuario usuario = HttpHelper.getUsuario(exchange);

        try {
            if (path.equals("/api/cursos") && method.equals("GET")) {
                handleGetAll(exchange);

            } else if (path.equals("/api/cursos") && method.equals("POST")) {
                if (!usuario.getRol().equals("admin")) {
                    HttpHelper.sendError(exchange, 403, "Acceso denegado");
                    return;
                }
                handleCreate(exchange);

            } else {
                HttpHelper.sendError(exchange, 404, "Ruta no encontrada");
            }

        } catch (IllegalArgumentException e) {
            // Errores de validación del Service llegan aquí como 400
            HttpHelper.sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            HttpHelper.sendError(exchange, 500, "Error interno del servidor");
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Curso> cursos = cursoService.getAll();
        // Serializar lista: stream → toMap() → toJsonArray()
        List<Map<String, Object>> lista = cursos.stream()
                .map(Curso::toMap)
                .collect(Collectors.toList());
        HttpHelper.sendJsonArray(exchange, 200, JsonUtil.toJsonArray(lista));
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        Map<String, Object> body = HttpHelper.readJsonBody(exchange);
        Curso creado = cursoService.create(
            (String) body.get("nombre"),
            (String) body.get("codigo"),
            (int) body.get("profesorId"),    // leer int del body
            (int) body.get("semestreId")
        );
        HttpHelper.sendJson(exchange, 201, creado.toMap()); // 201 = creado exitosamente
    }
}
```

---

## Registrar la ruta — Application.java

Cuando el controller esté listo, descomentar la línea correspondiente:

```java
server.createContext("/api/cursos", new AuthMiddleware(new CursoController()));
```

---

## SQL — Guía práctica

El backend usa **JDBC** para hablar con PostgreSQL. Nunca se escribe SQL directamente como texto plano — siempre se usan **PreparedStatements** con `?` para evitar inyección SQL.

```java
// ❌ MAL — vulnerable a SQL injection
String sql = "SELECT * FROM usuario WHERE correo = '" + correo + "'";

// ✅ BIEN — siempre usar ? y setString/setInt
String sql = "SELECT * FROM usuario WHERE correo = ?";
stmt.setString(1, correo); // el 1 es la posición del ?
```

---

### SELECT — consultar datos

```sql
-- Traer todos los registros de una tabla
SELECT * FROM curso ORDER BY id;

-- Filtrar por una condición
SELECT * FROM curso WHERE id = ?;

-- Filtrar por dos condiciones
SELECT * FROM nota WHERE estudiante_id = ? AND corte_id = ?;

-- Filtrar solo activos
SELECT * FROM usuario WHERE activo = true ORDER BY nombre;

-- Filtrar por rol
SELECT * FROM usuario WHERE rol = ? AND activo = true;
```

En Java:
```java
String sql = "SELECT * FROM curso WHERE id = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setInt(1, id);           // asigna el primer ?
ResultSet rs = stmt.executeQuery();
if (rs.next()) {              // si hay resultado
    return mapRow(rs);        // convertir fila → objeto Java
}
```

---

### INSERT — crear un registro

```sql
-- Crear un curso
INSERT INTO curso (nombre, codigo, profesor_id, semestre_id)
VALUES (?, ?, ?, ?)
RETURNING *;    -- PostgreSQL devuelve la fila creada con su id generado
```

En Java:
```java
String sql = "INSERT INTO curso (nombre, codigo, profesor_id, semestre_id) VALUES (?, ?, ?, ?) RETURNING *";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, curso.getNombre());
stmt.setString(2, curso.getCodigo());
stmt.setInt(3, curso.getProfesorId());
stmt.setInt(4, curso.getSemestreId());
ResultSet rs = stmt.executeQuery(); // executeQuery porque RETURNING devuelve filas
if (rs.next()) return mapRow(rs);   // retornar el objeto con el id asignado por la BD
```

---

### UPDATE — editar un registro

```sql
-- Editar nombre y correo de un usuario
UPDATE usuario SET nombre = ?, correo = ?, rol = ? WHERE id = ?
RETURNING *;

-- Desactivar usuario (no borrar)
UPDATE usuario SET activo = false WHERE id = ?;

-- Editar el valor de una nota
UPDATE nota SET valor = ? WHERE id = ?
RETURNING *;
```

En Java:
```java
String sql = "UPDATE nota SET valor = ? WHERE id = ? RETURNING *";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setDouble(1, nota.getValor());
stmt.setInt(2, nota.getId());
ResultSet rs = stmt.executeQuery();
if (rs.next()) return mapRow(rs);
```

---

### DELETE — eliminar un registro

```sql
-- Eliminar una nota por su id
DELETE FROM nota WHERE id = ?;

-- Eliminar matrícula de un estudiante en un curso
DELETE FROM matricula WHERE curso_id = ? AND estudiante_id = ?;
```

En Java:
```java
String sql = "DELETE FROM nota WHERE id = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setInt(1, id);
return stmt.executeUpdate() > 0; // retorna true si eliminó al menos 1 fila
```

---

### SUM y COUNT — funciones de agregación

```sql
-- Sumar todos los porcentajes de los cortes de un curso
-- (para validar que no supere 100%)
SELECT COALESCE(SUM(porcentaje), 0) AS total FROM corte WHERE curso_id = ?;

-- Contar cuántos cortes tiene un curso
-- (para generar el nombre: "Corte 1", "Corte 2"...)
SELECT COUNT(*) AS total FROM corte WHERE curso_id = ?;
```

En Java:
```java
// Ejemplo: sumar porcentajes
String sql = "SELECT COALESCE(SUM(porcentaje), 0) AS total FROM corte WHERE curso_id = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setInt(1, cursoId);
ResultSet rs = stmt.executeQuery();
if (rs.next()) return rs.getDouble("total");

// Ejemplo: contar cortes para generar nombre
String sqlCount = "SELECT COUNT(*) AS total FROM corte WHERE curso_id = ?";
// ... mismo patrón
// Con el resultado: "Corte " + (total + 1)  →  "Corte 1", "Corte 2"...
```

---

### JOIN — combinar dos tablas

Útil cuando necesitas datos de dos tablas a la vez.

```sql
-- Ver las notas de un curso con el nombre del estudiante
SELECT n.id, n.valor, u.nombre AS estudiante, c.porcentaje
FROM nota n
JOIN usuario u ON u.id = n.estudiante_id
JOIN corte  c ON c.id = n.corte_id
WHERE c.curso_id = ?;

-- Ver los estudiantes matriculados en un curso con su nombre
SELECT u.id, u.nombre, u.correo, m.fecha
FROM matricula m
JOIN usuario u ON u.id = m.estudiante_id
WHERE m.curso_id = ?
ORDER BY u.nombre;
```

En Java con JOIN, el `mapRow` lee las columnas por nombre:
```java
// Si hay columnas con el mismo nombre en dos tablas, usar alias (AS)
rs.getInt("id")            // columna id
rs.getString("estudiante") // alias del JOIN
rs.getDouble("porcentaje") // columna de la tabla corte
```

---

### Tabla de métodos JDBC según tipo de dato

| Tipo en Java | Método para asignar `?` | Método para leer del ResultSet |
|---|---|---|
| `int` | `stmt.setInt(pos, valor)` | `rs.getInt("columna")` |
| `String` | `stmt.setString(pos, valor)` | `rs.getString("columna")` |
| `double` | `stmt.setDouble(pos, valor)` | `rs.getDouble("columna")` |
| `boolean` | `stmt.setBoolean(pos, valor)` | `rs.getBoolean("columna")` |

---

## Probar con Postman

1. Importar `postman/SistemaNotas.postman_collection.json`
2. Correr **Login** primero — el token se guarda automáticamente en `{{token}}`
3. Todos los demás requests lo usan sin que tengas que copiarlo

**Usuarios de prueba:**

| Correo | Password | Rol |
|--------|----------|-----|
| admin@notas.com | 1234 | admin |
| carlos@notas.com | 1234 | profesor |
| juan@notas.com | 1234 | estudiante |
