# Tareas del Equipo — Sistema de Notas

> Para el patrón de código, ejemplos y explicación de HTTP ver **DOCUMENTACION.md**

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

## Edwin — Semestres + Vista del Estudiante (EP-06, EP-08)

> Semestres no depende de nadie 

### Parte 1 — Semestres (empezar aquí)

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

### Parte 2 — Vista del Estudiante (hacer después de que Alejandro termine NotaRepository)

**Archivos a crear:**
- `MisNotasController.java` — endpoints:
  - `GET /api/mis-notas` → notas del estudiante autenticado agrupadas por curso
  - `GET /api/mis-notas/promedio` → promedio general de todas sus materias

**Cómo obtener el ID del estudiante autenticado:**
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

## Jimmi — Cursos + Matrículas (EP-03)

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

> **`GET /api/mis-cursos`** usa el token para saber quién pregunta:
> ```java
> Usuario profesor = HttpHelper.getUsuario(exchange);
> List<Curso> cursos = cursoService.getByProfesor(profesor.getId());
> ```

**Al terminar**, descomentar en `Application.java`:
```java
server.createContext("/api/cursos", new AuthMiddleware(new CursoController()));
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

## Alejandro — Cortes + Notas (EP-04, EP-05)

### Parte 1 — Cortes

**Archivos a crear:**
- `CorteRepository.java` — métodos: `findByCurso(int cursoId)`, `findById(int id)`, `save(Corte c)`, `delete(int id)`, `sumaPorcentajes(int cursoId)`
- `CorteService.java` — validar:
  - porcentaje entre 1 y 100
  - la suma total de cortes del curso no supere 100%
- `CorteController.java` — endpoints:
  - `GET /api/cortes?cursoId=1` → lista cortes de un curso, **ordenados por id ASC**

> **Nombre de los cortes:** `Corte` no tiene campo `nombre`. El frontend genera
> "Corte 1", "Corte 2"... usando el índice del array. Por eso es obligatorio
> devolver los cortes **ordenados por id** para que el orden sea siempre consistente.
>
> En el SQL usar: `ORDER BY id ASC`
  - `POST /api/cortes` → crea corte (solo profesor o admin)
    ```json
    { "cursoId": 1, "porcentaje": 30 }
    ```
  - `DELETE /api/cortes/{id}` → elimina corte (solo profesor o admin)

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
  - calcular nota definitiva: `SUM(nota.valor * corte.porcentaje / 100)`
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

## Resumen de archivos por persona

| Persona | Archivos a crear | Depende de |
|---------|-----------------|------------|
| Edwin | `SemestreRepository`, `SemestreController`, `MisNotasController` | Parte 2 depende de Alejandro |
| Jimmi | `CursoRepository`, `CursoService`, `CursoController`, `MatriculaRepository`, `MatriculaController` | Edwin (semestres) |
| Alejandro | `CorteRepository`, `CorteService`, `CorteController`, `NotaRepository`, `NotaService`, `NotaController` | Jimmi (cursos) |
