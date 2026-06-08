# Sistema de Gestión de Notas Estudiantiles

Aplicación web para el registro, control y consulta de calificaciones académicas universitarias. Permite a profesores registrar notas por corte, a estudiantes consultar su rendimiento en tiempo real y al administrador gestionar usuarios, semestres y cursos.

---

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Backend | Java 17 — `com.sun.net.httpserver` (sin frameworks) |
| Base de datos | PostgreSQL 14 |
| Frontend | HTML, CSS, JavaScript, Vue 3 (CDN) |
| Estilos | Bootstrap 5.3 (CDN) |
| IDE | Apache NetBeans |
| Pruebas API | Postman |

---

## Requisitos previos

- [Java 17+](https://www.oracle.com/java/technologies/downloads/)
- [PostgreSQL 14+](https://www.postgresql.org/download/) — durante la instalación en Windows, anota la contraseña del superusuario que configures
- [pgAdmin 4](https://www.pgadmin.org/download/) — viene incluido en el instalador de PostgreSQL
- [Apache NetBeans](https://netbeans.apache.org/)
- [Postman](https://www.postman.com/downloads/)
- [VS Code](https://code.visualstudio.com/) con la extensión **Live Server**

---

## Configuración de la base de datos

Todo se hace desde **pgAdmin**. Ábrelo y conéctate al servidor local con las credenciales que configuraste al instalar PostgreSQL.

### 1. Crear el rol que usa el backend

En pgAdmin, clic derecho sobre el servidor → **Query Tool** y ejecuta:

```sql
CREATE ROLE postgres SUPERUSER LOGIN PASSWORD 'admin';
```

> Si durante la instalación de PostgreSQL ya creaste un usuario llamado `postgres`, omite este paso.

### 2. Crear la base de datos

En el panel izquierdo, clic derecho en **Databases** → **Create** → **Database...**

- **Database:** `sistema_notas`
- **Owner:** `postgres`
- Clic en **Save**

### 3. Crear las tablas

En pgAdmin, selecciona la base de datos `sistema_notas` → clic en **Query Tool**.

1. Abre el archivo `db/schema.sql` con cualquier editor de texto (Bloc de notas, VS Code)
2. Copia todo el contenido y pégalo en el Query Tool de pgAdmin
3. Clic en **Execute / Run** (▶) o presiona `F5`

### 4. Cargar los datos de prueba

Con el mismo Query Tool abierto sobre `sistema_notas`:

1. Abre el archivo `db/seed.sql`
2. Copia todo el contenido y pégalo en el Query Tool
3. Clic en **Execute / Run** (▶) o presiona `F5`

Deberías ver los datos en **sistema_notas → Schemas → Tables**.

---

## Correr el backend

1. Abrir el proyecto en **NetBeans**: `File → Open Project → backend/SistemaNotas-Api`
2. Clic en **Run Project** (F6)
3. El servidor inicia en `http://localhost:8080`

Deberías ver en la consola:
```
=================================
  API Sistema de Notas iniciada
  http://localhost:8080
=================================
```

---

## Correr el frontend

1. Abrir la carpeta `frontend/` en VS Code
2. Instalar la extensión **Live Server** si no la tienes: panel de extensiones (`Ctrl+Shift+X`) → buscar "Live Server" → Instalar
3. Clic derecho sobre `index.html` → **Open with Live Server**
4. El frontend se abre automáticamente en `http://localhost:5500`

---

## Probar la API con Postman

1. Importar la colección: `postman/SistemaNotas.postman_collection.json`
2. Correr el request **Login** — el token se guarda automáticamente
3. Todos los demás requests ya llevan el token en el header

**Usuarios de prueba:**

| Correo | Password | Rol |
|--------|----------|-----|
| admin@notas.com | 1234 | admin |
| carlos@notas.com | 1234 | profesor |
| laura@notas.com | 1234 | profesor |
| juan@notas.com | 1234 | estudiante |
| maria@notas.com | 1234 | estudiante |

---

## Estructura del proyecto

```
SistemaNotas-U/
├── backend/
│   └── SistemaNotas-Api/
│       └── src/
│           ├── config/        → Conexión a PostgreSQL
│           ├── model/         → Clases del dominio (Usuario, Curso, Nota...)
│           ├── repository/    → Consultas SQL
│           ├── service/       → Reglas de negocio
│           ├── controller/    → Endpoints HTTP
│           ├── middleware/    → Validación de token
│           ├── util/          → HttpHelper, JsonUtil, SessionManager
│           └── notas/         → Application.java (punto de entrada)
├── frontend/
│   ├── index.html             → Login
│   ├── pages/                 → Vistas por rol (admin/, profesor/, estudiante/)
│   ├── css/                   → Estilos globales
│   └── js/                    → api.js, auth.js, utils.js, components.js
├── db/
│   ├── schema.sql             → Definición de tablas
│   └── seed.sql               → Datos de prueba
├── postman/
│   └── SistemaNotas.postman_collection.json
├── docs/
│   ├── DOCUMENTACION.md       → Guía técnica backend
│   ├── DOCUMENTACION_FRONTEND.md → Guía técnica frontend (Vue, Bootstrap, API)
│   ├── TAREAS.md              → Tareas backend por persona
│   ├── TAREAS_FRONTEND.md     → Tareas frontend por persona
│   ├── FRONTEND_GUIA.md       → Patrones y convenciones frontend
│   └── BACKEND_STATUS.md      → Estado del backend
└── README.md
```

---

## Endpoints disponibles

| Método | Ruta | Descripción | Rol requerido |
|--------|------|-------------|---------------|
| POST | `/api/auth/login` | Iniciar sesión | Público |
| POST | `/api/auth/logout` | Cerrar sesión | Autenticado |
| GET | `/api/usuarios` | Listar usuarios | Admin |
| POST | `/api/usuarios` | Crear usuario | Admin |
| PUT | `/api/usuarios/{id}` | Editar usuario | Admin |
| DELETE | `/api/usuarios/{id}` | Desactivar usuario | Admin |
| GET | `/api/semestres` | Listar semestres | Autenticado |
| POST | `/api/semestres` | Crear semestre | Admin |
| GET | `/api/cursos` | Listar todos los cursos | Admin |
| GET | `/api/cursos/{id}` | Obtener un curso | Autenticado |
| GET | `/api/mis-cursos` | Cursos del profesor autenticado | Profesor |
| POST | `/api/cursos` | Crear curso | Admin |
| GET | `/api/matriculas?cursoId=1` | Estudiantes de un curso | Autenticado |
| POST | `/api/matriculas` | Matricular estudiante | Admin |
| DELETE | `/api/matriculas` | Desmatricular estudiante | Admin |
| GET | `/api/cortes?cursoId=1` | Cortes de un curso | Autenticado |
| POST | `/api/cortes` | Crear corte | Profesor / Admin |
| DELETE | `/api/cortes/{id}` | Eliminar corte | Profesor / Admin |
| GET | `/api/notas?cursoId=1` | Notas de un curso | Profesor / Admin |
| POST | `/api/notas` | Registrar nota | Profesor |
| PUT | `/api/notas/{id}` | Editar nota | Profesor |
| DELETE | `/api/notas/{id}` | Eliminar nota | Profesor |
| GET | `/api/mis-notas` | Mis notas por curso | Estudiante |
| GET | `/api/mis-notas/promedio` | Mi promedio general | Estudiante |

---

## Equipo

| Nombre | Rol | Módulo |
|--------|-----|--------|
| David Basto | Líder / Dev | Infraestructura, Auth, Usuarios |
| Alejandro Alisajar | Dev | Cortes, Notas |
| Jimmi Calvo | Dev | Cursos, Matrículas |
| Edwin Angulo | Dev | Semestres, Vista estudiante |

---

## Institución

**Institución Universitaria Antonio José Camacho**
Ingeniería de Software — 441A
2026
