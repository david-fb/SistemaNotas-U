# Backend Sistema de Notas - Estado Final

**Última actualización:** 2026-06-07  
**Estado:** 100% Completado

---

## Módulos Implementados

### 1. Base del Proyecto
- Infrastructure: DatabaseConfig, HttpHelper, SessionManager, AuthMiddleware
- Authentication: AuthService, AuthController (login/logout)
- Users: UsuarioRepository, UsuarioService, UsuarioController (CRUD)
- Models: Usuario, Semestre, Curso, Matricula, Corte, Nota
- Database: schema.sql, seed.sql

### 2. Semestres
- SemestreRepository (findAll, save)
- SemestreService (field validation)
- SemestreController (GET, POST)

### 3. Cursos + Matrículas
**Cursos:**
- Endpoints: GET /api/cursos, GET /api/cursos/{id}, GET /api/mis-cursos, POST /api/cursos
- Roles: admin can list all, profesor can see their courses

**Matrículas:**
- Endpoints: GET /api/matriculas?cursoId=X, POST, DELETE
- Note: fecha auto-generated with LocalDate.now() if not provided

### 4. Cortes + Notas
**Cortes:**
- Endpoints: GET /api/cortes/curso/{id}, POST, DELETE
- Validation: porcentaje sum per course cannot exceed 100%

**Notas:**
- Endpoints: GET /api/notas/curso/{id}, POST, PUT, DELETE
- MisNotasController: GET /api/mis-notas, GET /api/mis-notas/promedio
- Validation: value 0.0-5.0, student must be enrolled

---

## Endpoints Summary

Total: 26 endpoints

| Resource | Methods | Count |
|----------|---------|-------|
| Auth | login, logout | 2 |
| Usuarios | GET, POST, PUT, DELETE | 5 |
| Semestres | GET, POST | 2 |
| Cursos | GET (all/id/mine), POST | 4 |
| Matrículas | GET, POST, DELETE | 3 |
| Cortes | GET, POST, DELETE | 3 |
| Notas | GET, POST, PUT, DELETE | 4 |
| Mis Notas | GET (list, promedio) | 2 |

---

## Architecture

**Pattern:** MVC (Controller → Service → Repository → Model)

**Layers:**
- Controllers: routing, CORS, role validation
- Services: business logic, field validation
- Repositories: SQL queries with PreparedStatement
- Models: data objects with toMap() for JSON

**Authentication:** Bearer token in Authorization header

**Roles:** admin, profesor, estudiante (lowercase)

---

## Fixes Applied

1. MatriculaController: Changed role check from "ADMIN" to "admin" for consistency
2. MatriculaService: Made fecha optional, auto-generates with LocalDate.now()
3. SemestreController: Added proper try-catch-finally for exchange closure
4. Endpoint sync: Confirmed all routes use path params where specified (e.g., /api/cortes/curso/{id})

---

## File Structure

```
backend/SistemaNotas-Api/src/
├── controller/     (8 files)
├── repository/     (6 files)
├── service/        (7 files)
├── model/          (6 files)
├── middleware/     (1 file)
├── util/           (4 files)
└── notas/          (Application.java)
```

---

## Key Points for Frontend Development

**Authentication:**
- POST /api/auth/login returns {token: "..."}
- Include token in all requests: Authorization: Bearer <token>

**Route Conventions:**
- Path params for IDs: /api/resource/{id}
- Path params for filters: /api/cortes/curso/{id}, /api/notas/curso/{id}
- Query params: ?cursoId=X&estudianteId=Y

**Validation Rules:**
- Cortes: returned ordered by id ASC (important for frontend)
- Notas: value 0.0-5.0, approved if final_grade >= 3.0
- Matrículas: fecha auto-generated, frontend sends only {cursoId, estudianteId}
- Students must be enrolled in course to receive grades

**Response Format:**
- Arrays: [{id, ...fields}]
- Single objects: {id, ...fields}
- Errors: {error: "message"}
- Success without data: {mensaje: "text"}

---

## Documentation Files

- TAREAS.md: Complete endpoint specification with HTTP methods and examples
- postman/SistemaNotas.postman_collection.json: 26 endpoints ready to test
- BACKEND_STATUS.md: This file

---

## Next Steps

Frontend implementation:
- Login/logout flows
- Role-based dashboards (admin/profesor/estudiante)
- User management (create, edit, delete)
- Semester and course management
- Grade entry and viewing
- Reports

All backend endpoints are documented in Postman and ready for integration.
