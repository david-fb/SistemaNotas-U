# Guia Frontend — Sistema de Notas

## Stack

- **Bootstrap 5.3** (CDN) — estilos, modales, tablas, formularios
- **Vue 3** (CDN, modo global) — reactividad sin build tools
- **api.js** — fetch centralizado con token automatico
- **auth.js** — login, logout, guards de sesion y rol
- **utils.js** — toasts, confirmaciones, formateo

---

## Como correr el proyecto

1. Abrir la carpeta `frontend/` con VS Code
2. Instalar extension **Live Server**
3. Click derecho en `index.html` → "Open with Live Server"
4. El frontend corre en `http://localhost:5500`
5. El backend debe estar corriendo en `http://localhost:8080`

**Usuarios de prueba:**

| Correo | Password | Rol |
|--------|----------|-----|
| admin@notas.com | 1234 | admin |
| carlos@notas.com | 1234 | profesor |
| juan@notas.com | 1234 | estudiante |

---

## Estructura de carpetas

```
frontend/
├── index.html                 ← Login
├── css/global.css             ← Variables custom y overrides
├── js/
│   ├── api.js                 ← Fetch centralizado (NO TOCAR)
│   ├── auth.js                ← Sesion y guards (NO TOCAR)
│   └── utils.js               ← Helpers compartidos
└── pages/
    ├── dashboard.html         ← Menu por rol
    ├── admin/                 ← Ya implementado (ejemplo)
    │   ├── usuarios.html
    │   ├── semestres.html
    │   ├── cursos.html
    │   └── matriculas.html
    ├── profesor/              ← POR HACER
    │   ├── cursos.html
    │   ├── actividades.html
    │   └── notas.html
    └── estudiante/            ← POR HACER
        └── mis-notas.html
```

---

## Patron para crear una pagina nueva

Cada pagina sigue esta estructura. Copiar `admin/semestres.html` como punto de partida.

### 1. Head — CDNs y CSS

```html
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
<link rel="stylesheet" href="../../css/global.css">
```

### 2. Body — Contenido con Vue

```html
<div id="app">
    <!-- Navbar automatico segun rol -->
    <nav-bar active="mi-pagina"></nav-bar>

    <!-- Contenido principal con directivas Vue -->
</div>
```

> El prop `active` debe coincidir con el `id` del link en `components.js`.
> El navbar muestra los links del rol del usuario logueado automaticamente.

### 3. Scripts — Orden importante

```html
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js" integrity="sha384-FKyoEForCGlyvwx9Hj09JcYn3nv7wiPVlz7YYwJrWVcXK/BmnVDxM+D2scQbITxI" crossorigin="anonymous"></script>
<script src="https://unpkg.com/vue@3/dist/vue.global.js"></script>
<script src="../../js/api.js"></script>
<script src="../../js/auth.js"></script>
<script src="../../js/utils.js"></script>
<script src="../../js/components.js"></script>
```

### 4. App Vue — Siempre el mismo esqueleto

```html
<script>
    // Guard: verificar que el usuario tiene el rol correcto
    if (!Auth.requireRol('profesor')) {}

    Vue.createApp({
        data() {
            return {
                items: [],           // datos de la tabla
                form: {},            // campos del formulario
                formError: '',       // mensaje de error del modal
                saving: false        // loading del boton guardar
            }
        },
        mounted() { this.loadData(); },
        methods: {
            async loadData() {
                try {
                    this.items = await Api.get('/endpoint');
                } catch (e) {
                    Utils.toast('Error al cargar', 'danger');
                }
            },

            openCreate() {
                this.form = { /* campos vacios */ };
                this.formError = '';
                new bootstrap.Modal(document.getElementById('miModal')).show();
            },

            async save() {
                this.formError = '';
                this.saving = true;
                try {
                    await Api.post('/endpoint', this.form);
                    Utils.toast('Creado exitosamente');
                    bootstrap.Modal.getInstance(document.getElementById('miModal')).hide();
                    await this.loadData();
                } catch (e) {
                    this.formError = e.message;
                } finally {
                    this.saving = false;
                }
            },

            async remove(id) {
                if (!await Utils.confirm('¿Eliminar este registro?')) return;
                try {
                    await Api.delete('/endpoint/' + id);
                    Utils.toast('Eliminado');
                    await this.loadData();
                } catch (e) {
                    Utils.toast(e.message, 'danger');
                }
            }
        }
    }).component('nav-bar', NavBar).mount('#app');
</script>
```

---

## Componente NavBar (components.js)

El navbar se genera automaticamente segun el rol del usuario. Solo necesitas:

1. Agregar `<nav-bar active="id-pagina"></nav-bar>` en el HTML
2. Importar `components.js` en los scripts
3. Registrar con `.component('nav-bar', NavBar)` antes de `.mount('#app')`

Los links de cada rol se definen en `components.js`. Si necesitas agregar una pagina nueva al menu, edita el array del rol correspondiente ahi.

---

## Referencia rapida — Api.js

```javascript
Api.get('/cursos')              // GET sin body
Api.post('/cursos', { ... })    // POST con body JSON
Api.put('/cursos/1', { ... })   // PUT con body JSON
Api.delete('/cursos/1')         // DELETE sin body
```

El token se agrega automaticamente. Si el servidor responde 401, redirige al login.

---

## Referencia rapida — Utils.js

```javascript
Utils.toast('Mensaje', 'success')    // Toast verde (exito)
Utils.toast('Error!', 'danger')      // Toast rojo (error)
Utils.confirm('¿Seguro?')           // Confirmacion (retorna true/false)
Utils.formatDate('2025-01-20')      // Retorna "20/01/2025"
```

---

## Directivas Vue mas usadas

| Directiva | Uso | Ejemplo |
|-----------|-----|---------|
| `v-for` | Repetir elementos | `<tr v-for="u in users">` |
| `v-model` | Bind input ↔ data | `<input v-model="form.nombre">` |
| `v-if` | Mostrar/ocultar | `<div v-if="error">` |
| `@click` | Evento click | `<button @click="save">` |
| `@submit.prevent` | Enviar form sin recargar | `<form @submit.prevent="save">` |
| `:disabled` | Deshabilitar dinamico | `:disabled="saving"` |
| `:class` | Clase dinamica | `:class="activo ? 'text-success' : 'text-danger'"` |

---

## Paginas pendientes por rol

### Profesor

**profesor/cursos.html** — Mis Cursos
- Guard: `Auth.requireRol('profesor')`
- Endpoint: `GET /api/mis-cursos`
- Muestra tabla con los cursos asignados al profesor
- Puede ver estudiantes matriculados: `GET /api/matriculas?cursoId=X`

**profesor/actividades.html** — Cortes
- Seleccionar un curso de mis-cursos
- Listar cortes: `GET /api/cortes/curso/{id}`
- Crear corte: `POST /api/cortes` con `{ cursoId, porcentaje }`
- Eliminar corte: `DELETE /api/cortes/{id}`
- Mostrar la suma actual de porcentajes (no puede superar 100%)
- Los cortes se nombran por indice: "Corte 1", "Corte 2"...

**profesor/notas.html** — Registro de Notas
- Seleccionar un curso de mis-cursos
- Listar notas: `GET /api/notas/curso/{id}`
- Crear nota: `POST /api/notas` con `{ estudianteId, corteId, valor }`
- Editar nota: `PUT /api/notas/{id}` con `{ valor }`
- Eliminar nota: `DELETE /api/notas/{id}`
- Valor entre 0.0 y 5.0
- Solo estudiantes matriculados pueden recibir notas

### Estudiante

**estudiante/mis-notas.html** — Mis Notas
- Guard: `Auth.requireRol('estudiante')`
- Endpoint: `GET /api/mis-notas`
- Muestra notas agrupadas por curso
- Calcular definitiva: suma(valor * porcentaje / 100)
- Endpoint promedio: `GET /api/mis-notas/promedio`
- >= 3.0 es "Aprobado" (badge verde), < 3.0 es "Reprobado" (badge rojo)

---

## Tips

- Siempre usar `Number()` al enviar IDs en el body (los selects retornan strings)
- Para cargar datos de multiples endpoints usar `Promise.all([])`
- El modal se abre con `new bootstrap.Modal(element).show()`
- El modal se cierra con `bootstrap.Modal.getInstance(element).hide()`
- Despues de crear/editar/eliminar, siempre llamar `loadData()` para refrescar la tabla
