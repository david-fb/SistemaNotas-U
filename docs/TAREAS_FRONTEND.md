# Tareas del Equipo — Frontend Sistema de Notas

> Antes de empezar, leer **FRONTEND_GUIA.md** y **DOCUMENTACION_FRONTEND.md** (ambos en esta carpeta docs/)
> El ejemplo completo a seguir es `pages/admin/usuarios.html`

---

## David — Base del Frontend ✅ COMPLETADO

Todo lo siguiente ya está hecho y funciona:

- Login con Vue + Bootstrap (`index.html`)
- Dashboard con menú dinámico por rol (`pages/dashboard.html`)
- Módulo API centralizado (`js/api.js`) — todos los fetch pasan por aquí
- Autenticación y guards (`js/auth.js`) — login, logout, requireRol
- Helpers compartidos (`js/utils.js`) — toasts, confirmaciones, formateo
- Componente NavBar (`js/components.js`) — navbar automático según rol
- CSS con overrides (`css/global.css`)
- Panel de Admin completo: usuarios, semestres, cursos, matrículas
- Guía y documentación para el equipo

---

## Edwin — Profesor: Mis Cursos (`pages/profesor/cursos.html`)

> Módulo independiente. Puedes empezar el día 1 sin esperar a nadie.
> Usa `pages/admin/matriculas.html` como referencia — tiene un patrón similar (seleccionar algo → ver lista).

**Guard:** `Auth.requireRol('profesor');`

**Qué debe hacer esta página:**

1. Mostrar la lista de cursos del profesor logueado
2. Al hacer click en un curso, mostrar los estudiantes matriculados

**Orden de implementación:**

1. Crear el archivo `pages/profesor/cursos.html` copiando la estructura de cualquier página admin
2. Cambiar el guard a `Auth.requireRol('profesor')`
3. Cambiar el `active` del nav-bar a `active="mis-cursos"`
4. En `mounted()`, cargar los cursos con `Api.get('/mis-cursos')`
5. Mostrar los cursos en una tabla con `v-for`
6. Agregar un botón "Ver Estudiantes" en cada fila
7. Al hacer click, cargar matrículas con `Api.get('/matriculas?cursoId=' + id)`
8. Mostrar los estudiantes en una segunda tabla debajo

**Endpoints que vas a usar:**

| Método | Endpoint | Para qué |
|--------|----------|----------|
| GET | `/api/mis-cursos` | Lista los cursos del profesor logueado |
| GET | `/api/matriculas?cursoId=X` | Lista estudiantes matriculados en un curso |

**Ejemplo de cómo cargar los cursos:**

```javascript
async loadCursos() {
    try {
        this.cursos = await Api.get('/mis-cursos');
    } catch (e) {
        Utils.toast('Error al cargar cursos', 'danger');
    }
}
```

**Ejemplo de cómo ver estudiantes de un curso:**

```javascript
async verEstudiantes(cursoId) {
    this.selectedCurso = cursoId;
    try {
        this.matriculas = await Api.get('/matriculas?cursoId=' + cursoId);
    } catch (e) {
        Utils.toast('Error al cargar estudiantes', 'danger');
    }
}
```

**Tip:** Puedes usar `v-if="selectedCurso"` para mostrar la tabla de estudiantes solo cuando se selecciona un curso. Ver cómo `admin/matriculas.html` hace esto.

---

## Jimmi — Profesor: Actividades Evaluativas (`pages/profesor/actividades.html`)

> Módulo independiente. Puedes empezar el día 1 sin esperar a nadie.
> Usa `pages/admin/matriculas.html` como referencia — tiene el mismo patrón de "seleccionar curso → hacer algo".

**Guard:** `Auth.requireRol('profesor');`

**Qué debe hacer esta página:**

1. Mostrar un select con los cursos del profesor
2. Al seleccionar un curso, mostrar los cortes existentes en una tabla
3. Mostrar la suma actual de porcentajes (ej: "70% de 100% asignado")
4. Botón para crear un nuevo corte (modal con input de porcentaje)
5. Botón para eliminar un corte

**Orden de implementación:**

1. Crear `pages/profesor/actividades.html` copiando la estructura base
2. Cambiar guard y nav-bar: `Auth.requireRol('profesor')` y `active="actividades"`
3. En `mounted()`, cargar cursos con `Api.get('/mis-cursos')`
4. Agregar un `<select v-model="selectedCurso" @change="loadCortes">`
5. En `loadCortes()`, llamar `Api.get('/cortes/curso/' + id)`
6. Mostrar la tabla de cortes con `v-for` — nombrarlos "Corte 1", "Corte 2" usando el índice
7. Calcular la suma de porcentajes con una computed: `this.cortes.reduce((sum, c) => sum + c.porcentaje, 0)`
8. Agregar modal para crear corte con `POST /api/cortes`
9. Agregar botón eliminar con `DELETE /api/cortes/{id}`

**Endpoints que vas a usar:**

| Método | Endpoint | Para qué |
|--------|----------|----------|
| GET | `/api/mis-cursos` | Lista cursos del profesor |
| GET | `/api/cortes/curso/{id}` | Lista cortes de un curso (ordenados por id) |
| POST | `/api/cortes` | Crea un corte nuevo |
| DELETE | `/api/cortes/{id}` | Elimina un corte |

**Body del POST:**

```json
{ "cursoId": 1, "porcentaje": 30 }
```

**Cómo nombrar los cortes (no tienen nombre en la BD):**

```html
<tr v-for="(corte, index) in cortes" :key="corte.id">
    <td>Corte {{ index + 1 }}</td>
    <td>{{ corte.porcentaje }}%</td>
</tr>
```

**Cómo mostrar la suma de porcentajes:**

```javascript
computed: {
    sumaPorcentajes() {
        return this.cortes.reduce((sum, c) => sum + c.porcentaje, 0);
    }
}
```

```html
<p>Porcentaje asignado: <strong>{{ sumaPorcentajes }}%</strong> de 100%</p>
```

**Importante:** El backend ya valida que la suma no supere 100%. Si mandas un porcentaje que se pasa, el servidor responde con error y el `catch` del try muestra el mensaje en el modal.

---

## Alejandro — Profesor: Notas + Estudiante: Mis Notas

> Módulo independiente. Puedes empezar el día 1 sin esperar a nadie.
> Usa `pages/admin/usuarios.html` como referencia para el CRUD de notas.

### Parte 1 — Registro de Notas (`pages/profesor/notas.html`)

**Guard:** `Auth.requireRol('profesor');`

**Qué debe hacer esta página:**

1. Mostrar un select con los cursos del profesor
2. Al seleccionar un curso, cargar cortes y notas
3. Mostrar una tabla con: estudiante, nota de cada corte, nota definitiva
4. Botón para registrar/editar nota (modal con input numérico)
5. Botón para eliminar nota

**Orden de implementación:**

1. Crear `pages/profesor/notas.html` copiando la estructura base
2. Cambiar guard y nav-bar: `Auth.requireRol('profesor')` y `active="notas"`
3. En `mounted()`, cargar cursos con `Api.get('/mis-cursos')`
4. Al seleccionar curso, cargar en paralelo:
   ```javascript
   const [cortes, notas, matriculas] = await Promise.all([
       Api.get('/cortes/curso/' + cursoId),
       Api.get('/notas/curso/' + cursoId),
       Api.get('/matriculas?cursoId=' + cursoId)
   ]);
   ```
5. Armar la tabla: una fila por estudiante, una columna por corte
6. En cada celda, buscar si hay nota: `notas.find(n => n.estudianteId === est.id && n.corteId === corte.id)`
7. Si no hay nota, mostrar botón "Registrar". Si hay, mostrar el valor con botón "Editar"
8. Modal para registrar: `POST /api/notas` con `{ estudianteId, corteId, valor }`
9. Modal para editar: `PUT /api/notas/{id}` con `{ valor }`
10. Botón eliminar: `DELETE /api/notas/{id}`

**Endpoints que vas a usar:**

| Método | Endpoint | Para qué |
|--------|----------|----------|
| GET | `/api/mis-cursos` | Lista cursos del profesor |
| GET | `/api/cortes/curso/{id}` | Cortes del curso (columnas de la tabla) |
| GET | `/api/notas/curso/{id}` | Todas las notas del curso |
| GET | `/api/matriculas?cursoId=X` | Estudiantes matriculados (filas de la tabla) |
| POST | `/api/notas` | Registrar nota nueva |
| PUT | `/api/notas/{id}` | Editar valor de una nota |
| DELETE | `/api/notas/{id}` | Eliminar una nota |

**Body del POST:**

```json
{ "estudianteId": 4, "corteId": 1, "valor": 4.5 }
```

**Body del PUT:**

```json
{ "valor": 3.8 }
```

**Cómo buscar la nota de un estudiante en un corte:**

```javascript
getNota(estudianteId, corteId) {
    return this.notas.find(n => n.estudianteId === estudianteId && n.corteId === corteId);
}
```

**Cómo calcular la definitiva de un estudiante:**

```javascript
getDefinitiva(estudianteId) {
    let total = 0;
    for (const corte of this.cortes) {
        const nota = this.getNota(estudianteId, corte.id);
        if (nota) total += nota.valor * corte.porcentaje / 100;
    }
    return total.toFixed(1);
}
```

---

### Parte 2 — Vista del Estudiante (`pages/estudiante/mis-notas.html`)

**Guard:** `Auth.requireRol('estudiante');`

**Qué debe hacer esta página:**

1. Cargar las notas del estudiante logueado
2. Mostrar notas agrupadas por curso
3. Mostrar la nota definitiva de cada curso con badge Aprobado/Reprobado
4. Mostrar el promedio general

**Orden de implementación:**

1. Crear `pages/estudiante/mis-notas.html` copiando la estructura base
2. Cambiar guard y nav-bar: `Auth.requireRol('estudiante')` y `active="mis-notas"`
3. En `mounted()`, cargar datos:
   ```javascript
   const [misNotas, promedio] = await Promise.all([
       Api.get('/mis-notas'),
       Api.get('/mis-notas/promedio')
   ]);
   ```
4. Mostrar una card por cada curso con sus notas
5. Mostrar la definitiva con badge de color
6. Mostrar el promedio general arriba

**Endpoints que vas a usar:**

| Método | Endpoint | Para qué |
|--------|----------|----------|
| GET | `/api/mis-notas` | Notas del estudiante agrupadas por curso |
| GET | `/api/mis-notas/promedio` | Promedio general |

**Cómo mostrar el badge de aprobado/reprobado:**

```html
<span class="badge" :class="definitiva >= 3.0 ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'">
    {{ definitiva >= 3.0 ? 'Aprobado' : 'Reprobado' }}
</span>
```

**Tip:** Esta es la página más sencilla de todas — es solo lectura, no tiene modales ni formularios. Solo muestra datos.

---

## Resumen

| Persona | Páginas a crear | Complejidad | Referencia |
|---------|----------------|-------------|------------|
| David | Base + Admin (4 páginas) | - | ✅ COMPLETADO |
| Edwin | `profesor/cursos.html` | Baja | `admin/matriculas.html` |
| Jimmi | `profesor/actividades.html` | Media | `admin/matriculas.html` |
| Alejandro | `profesor/notas.html` + `estudiante/mis-notas.html` | Media-Alta | `admin/usuarios.html` |

**Archivos que NO deben modificar:**
- `js/api.js` — fetch centralizado
- `js/auth.js` — autenticación
- `js/utils.js` — helpers compartidos
- `js/components.js` — solo si necesitan agregar links al navbar de su rol

**Para agregar un link al navbar de profesor**, editar en `components.js` el array de profesor:

```javascript
profesor: [
    { id: 'mis-cursos', text: 'Mis Cursos', href: '/pages/profesor/cursos.html' },
    { id: 'actividades', text: 'Actividades', href: '/pages/profesor/actividades.html' },
    { id: 'notas', text: 'Notas', href: '/pages/profesor/notas.html' }
]
```
