# Tareas del Equipo — Sistema de Notas

> Para el patrón de código, ejemplos de SQL y explicación de HTTP ver **DOCUMENTACION.md**

---

## David — Base del proyecto ✅ COMPLETADO

Todo lo siguiente ya está hecho y funciona:

- Infraestructura: `DatabaseConfig`, `HttpHelper`, `JsonUtil`, `SessionManager`, `AuthMiddleware`
- Autenticación: `AuthService`, `AuthController` → `POST /api/auth/login` y `/logout`
- Usuarios completo: `UsuarioRepository`, `UsuarioService`, `UsuarioController`
- Todos los modelos: `Usuario`, `Semestre`, `Curso`, `Matricula`, `Corte`, `Nota`
- Base de datos: `schema.sql` y `seed.sql` listos
- Colección Postman lista para importar

---

## Edwin — Semestres (EP-08)

> Módulo completamente independiente. Puedes empezar el día 1 sin esperar a nadie.

**Orden de implementación:**

1. Crear `SemestreRepository.java` con `findAll()`
2. Crear `SemestreController.java` con solo `GET /api/semestres`
3. Descomentar la ruta en `Application.java` y probar en Postman que devuelve la lista
4. Agregar `save(Semestre s)` al repository
5. Agregar `POST /api/semestres` al controller
6. Probar en Postman que se crea un semestre nuevo

**Archivos a crear:**
- `SemestreRepository.java` — métodos: `findAll()`, `save(Semestre s)`
- `SemestreController.java` — endpoints:
  - `GET /api/semestres` → lista todos los semestres (todos los roles)
  - `POST /api/semestres` → crea semestre (solo admin)
    ```json
    { "nombre": "2025-2", "fechaInicio": "2025-07-14", "fechaFin": "2025-12-05" }
    ```

**Al terminar**, descomentar en `Application.java`:
```java
server.createContext("/api/semestres", new AuthMiddleware(new SemestreController()));
```

---

## Jimmi — Cursos + Matrículas (EP-03)

> Módulo independiente. Puedes empezar el día 1 — el seed ya tiene semestres y cursos de prueba.

**Orden de implementación:**

1. Crear `CursoRepository.java` con `findAll()` y `findById()`
2. Crear `CursoController.java` con solo `GET /api/cursos`
3. Descomentar la ruta en `Application.java` y probar en Postman que devuelve la lista
4. Agregar `findByCodigo()` y `save()` al repository
5. Crear `CursoService.java` con las validaciones
6. Agregar `POST /api/cursos` al controller y probar en Postman
7. Agregar `findByProfesor()` al repository
8. Agregar `GET /api/mis-cursos` al controller y probar con token de profesor
9. Crear `MatriculaRepository.java` con `findByCurso()` y `save()`
10. Crear `MatriculaController.java` con `GET` y `POST /api/matriculas` y probar
11. Agregar `delete()` al repository y `DELETE /api/matriculas` al controller

### Parte 1 — Cursos

**Archivos a crear:**
- `CursoRepository.java` — métodos: `findAll()`, `findById(int id)`, `findByCodigo(String codigo)`, `findByProfesor(int profesorId)`, `save(Curso c)`
- `CursoService.java` — validar:
  - nombre y código obligatorios
  - código único (usar `findByCodigo` para verificar)
- `CursoController.java` — endpoints:
  - `GET /api/cursos` → lista todos (solo admin)
  - `GET /api/cursos/{id}` → obtiene uno (todos los roles)
  - `GET /api/mis-cursos` → cursos del profesor autenticado (solo profesor)
  - `POST /api/cursos` → crea curso (solo admin)
    ```json
    { "nombre": "Ingeniería de Software", "codigo": "IS-501", "profesorId": 2, "semestreId": 1 }
    ```

> **`GET /api/mis-cursos`** usa el token para filtrar:
> ```java
> Usuario profesor = HttpHelper.getUsuario(exchange);
> List<Curso> cursos = cursoService.getByProfesor(profesor.getId());
> ```

**Al terminar**, descomentar en `Application.java`:
```java
server.createContext("/api/cursos",    new AuthMiddleware(new CursoController()));
server.createContext("/api/mis-cursos", new AuthMiddleware(new CursoController()));
```

---

### Parte 2 — Matrículas

**Archivos a crear:**
- `MatriculaRepository.java` — métodos: `findByCurso(int cursoId)`, `save(Matricula m)`, `delete(int cursoId, int estudianteId)`
- `MatriculaController.java` — endpoints:
  - `GET /api/matriculas?cursoId=1` → lista estudiantes de un curso
  - `POST /api/matriculas` → matricula un estudiante (solo admin)
    ```json
    { "cursoId": 1, "estudianteId": 4 }
    ```
  - `DELETE /api/matriculas?cursoId=1&estudianteId=4` → desmatricula (solo admin)

**Al terminar**, descomentar en `Application.java`:
```java
server.createContext("/api/matriculas", new AuthMiddleware(new MatriculaController()));
```

---

## Alejandro — Cortes + Notas + Vista del Estudiante (EP-04, EP-05, EP-06) ✅ COMPLETADO

> Módulo independiente. Puedes empezar el día 1 — el seed ya tiene cursos, cortes y notas de prueba.

**Orden de implementación:**

1. Crear `CorteRepository.java` con `findByCurso()` y `findById()`
2. Crear `CorteController.java` con solo `GET /api/cortes?cursoId=1`
3. Descomentar la ruta en `Application.java` y probar en Postman
4. Agregar `sumaPorcentajes()` y `save()` al repository
5. Crear `CorteService.java` con la validación de porcentajes
6. Agregar `POST /api/cortes` al controller y probar en Postman
7. Agregar `delete()` al repository y `DELETE /api/cortes/{id}` al controller
8. Crear `NotaRepository.java` con `findByCurso()`, `findByEstudiante()` y `findById()`
9. Crear `NotaController.java` con solo `GET /api/notas?cursoId=1` y probar
10. Crear `NotaService.java` con las validaciones
11. Agregar `save()` al repository y `POST /api/notas` al controller y probar
12. Agregar `update()` al repository y `PUT /api/notas/{id}` al controller
13. Agregar `delete()` al repository y `DELETE /api/notas/{id}` al controller
14. Crear `MisNotasController.java` con `GET /api/mis-notas` usando los repositories ya creados
15. Agregar `GET /api/mis-notas/promedio` y probar con token de estudiante

### Parte 1 — Cortes

**Archivos a crear:**
- `CorteRepository.java` — métodos: `findByCurso(int cursoId)`, `findById(int id)`, `save(Corte c)`, `delete(int id)`, `sumaPorcentajes(int cursoId)`
- `CorteService.java` — validar:
  - porcentaje entre 1 y 100
  - la suma total de cortes del curso no supere 100%
- `CorteController.java` — endpoints:
  - `GET /api/cortes?cursoId=1` → lista cortes de un curso, **ordenados por id ASC**
  - `POST /api/cortes` → crea corte (solo profesor o admin)
    ```json
    { "cursoId": 1, "porcentaje": 30 }
    ```
  - `DELETE /api/cortes/{id}` → elimina corte (solo profesor o admin)

> **Nombre de los cortes:** el modelo no tiene campo `nombre`. El frontend genera
> "Corte 1", "Corte 2"... usando el índice del array. Por eso es **obligatorio**
> devolver los cortes ordenados por id: `ORDER BY id ASC`

**Al terminar**, descomentar en `Application.java`:
```java
server.createContext("/api/cortes", new AuthMiddleware(new CorteController()));
```

---

### Parte 2 — Notas

**Archivos a crear:**
- `NotaRepository.java` — métodos: `findByCurso(int cursoId)`, `findByEstudiante(int estudianteId)`, `findById(int id)`, `save(Nota n)`, `update(Nota n)`, `delete(int id)`
- `NotaService.java` — validar:
  - valor entre 0.0 y 5.0
  - el estudiante debe estar matriculado en el curso del corte
- `NotaController.java` — endpoints:
  - `GET /api/notas?cursoId=1` → notas de todos los estudiantes del curso (vista profesor)
  - `POST /api/notas` → registra nota (solo profesor)
    ```json
    { "estudianteId": 4, "corteId": 1, "valor": 4.5 }
    ```
  - `PUT /api/notas/{id}` → edita nota (solo profesor)
    ```json
    { "valor": 3.8 }
    ```
  - `DELETE /api/notas/{id}` → elimina nota (solo profesor)

**Al terminar**, descomentar en `Application.java`:
```java
server.createContext("/api/notas", new AuthMiddleware(new NotaController()));
```

---

### Parte 3 — Vista del Estudiante

> Usa los mismos `CorteRepository` y `NotaRepository` que ya creaste arriba.

**Archivos a crear:**
- `MisNotasController.java` — endpoints:
  - `GET /api/mis-notas` → notas del estudiante autenticado agrupadas por curso
  - `GET /api/mis-notas/promedio` → promedio general de todas sus materias

**Cómo obtener el estudiante autenticado:**
```java
Usuario estudiante = HttpHelper.getUsuario(exchange);
int estudianteId = estudiante.getId();
```

**Fórmula de nota definitiva por curso:**
```java
double definitiva = 0;
for (Nota nota : notasDelCurso) {
    Corte corte = corteRepository.findById(nota.getCorteId());
    definitiva += nota.getValor() * corte.getPorcentaje() / 100;
}
// definitiva >= 3.0 → "Aprobado"
// definitiva <  3.0 → "Reprobado"
```

**Al terminar**, descomentar en `Application.java`:
```java
server.createContext("/api/mis-notas", new AuthMiddleware(new MisNotasController()));
```

---

## Resumen

| Persona | Archivos a crear | Depende de |
|---------|-----------------|------------|
| Edwin | `SemestreRepository`, `SemestreController` | Nadie ✅ |
| Jimmi | `CursoRepository`, `CursoService`, `CursoController`, `MatriculaRepository`, `MatriculaController` | Nadie ✅ |
| Alejandro | `CorteRepository`, `CorteService`, `CorteController`, `NotaRepository`, `NotaService`, `NotaController`, `MisNotasController` | Nadie ✅ |
