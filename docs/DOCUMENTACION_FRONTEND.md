# Documentación Frontend — Sistema de Notas

---

## ¿Cómo funciona el frontend?

El frontend es un conjunto de archivos **HTML** que se abren en el navegador. Cada página es independiente — no hay un framework que controle la navegación. Cuando haces click en un link, el navegador carga otro archivo `.html`.

```
Navegador                          Servidor Backend (Java)
   |                                        |
   |  1. Abro usuarios.html                |
   |  2. Vue monta la app                  |
   |  3. mounted() llama Api.get()         |
   |  GET /api/usuarios                    |
   | ------------------------------------> |
   |                                        |  consulta PostgreSQL
   |  [{ id: 1, nombre: "Admin", ... }]    |
   | <------------------------------------ |
   |  4. Vue actualiza la tabla             |
```

---

## Stack que usamos

| Tecnología | Para qué | Cómo se incluye |
|-----------|----------|-----------------|
| **Bootstrap 5** | Estilos: tablas, modales, botones, formularios | CDN en el `<head>` |
| **Vue 3** | Reactividad: cuando cambian los datos, la página se actualiza sola | CDN en `<script>` |
| **api.js** | Todas las peticiones HTTP al backend | `<script src="js/api.js">` |
| **auth.js** | Login, logout, verificar sesión y rol | `<script src="js/auth.js">` |
| **utils.js** | Toasts, confirmaciones, formateo de fechas | `<script src="js/utils.js">` |
| **components.js** | Componentes Vue reutilizables (navbar) | `<script src="js/components.js">` |

No necesitan instalar nada. Todo funciona con **Live Server** de VS Code.

---

## ¿Qué es Vue y por qué lo usamos?

Sin Vue, para mostrar una lista de usuarios tendrías que hacer algo así:

```javascript
// ❌ Sin Vue — manipular el DOM manualmente
const users = await Api.get('/usuarios');
const tbody = document.getElementById('tabla-body');
tbody.innerHTML = '';
users.forEach(u => {
    const tr = document.createElement('tr');
    tr.innerHTML = '<td>' + u.nombre + '</td><td>' + u.correo + '</td>';
    tbody.appendChild(tr);
});
```

Con Vue, los datos y el HTML están conectados. Cuando cambian los datos, la tabla se actualiza sola:

```javascript
// ✅ Con Vue — declarativo
data() {
    return { users: [] }
},
mounted() {
    this.users = await Api.get('/usuarios');
    // la tabla se actualiza automáticamente
}
```

```html
<tr v-for="u in users">
    <td>{{ u.nombre }}</td>
    <td>{{ u.correo }}</td>
</tr>
```

---

## Anatomía de una página

Cada página `.html` del proyecto tiene esta estructura. Entender esto es clave.

```
┌──────────────────────────────────────────┐
│ <head>                                    │
│   Bootstrap CSS (CDN)                    │
│   global.css (overrides)                 │
│                                          │
│ <body>                                    │
│   <div id="app">                         │  ← Vue controla todo dentro de #app
│     <nav-bar active="...">               │  ← Componente navbar
│     ... contenido de la página ...       │  ← Tablas, formularios, modales
│   </div>                                 │
│                                          │
│   Bootstrap JS (CDN)                     │  ← Orden importa
│   Vue 3 (CDN)                            │
│   api.js                                 │
│   auth.js                                │
│   utils.js                               │
│   components.js                          │
│                                          │
│   <script>                               │
│     Auth.requireRol('...');              │  ← Guard de seguridad
│     Vue.createApp({ ... })               │  ← La app Vue de esta página
│       .component('nav-bar', NavBar)      │  ← Registrar navbar
│       .mount('#app');                    │  ← Conectar Vue al HTML
│   </script>                              │
└──────────────────────────────────────────┘
```

**¿Por qué el orden de los scripts importa?**
- `api.js` va primero porque `auth.js` lo usa para hacer login
- `auth.js` va antes de `utils.js` porque el navbar necesita `Auth.getUsuario()`
- `components.js` va al final porque usa tanto `Auth` como `Utils`
- Vue va antes de todo porque `Vue.createApp()` lo necesita disponible

---

## Vue — Conceptos básicos

### data() — Los datos de la página

Todo lo que la página necesita "recordar" va dentro de `data()`. Vue observa estos datos y actualiza el HTML cuando cambian.

```javascript
data() {
    return {
        users: [],           // lista para la tabla
        search: '',          // texto del buscador
        form: {              // campos del formulario
            nombre: '',
            correo: ''
        },
        editing: false,      // ¿estamos editando o creando?
        saving: false        // ¿el botón está cargando?
    }
}
```

### mounted() — Se ejecuta cuando la página carga

Es como un `window.onload`. Aquí se cargan los datos iniciales.

```javascript
mounted() {
    this.loadUsers();  // cargar datos al entrar a la página
}
```

### methods — Las funciones de la página

Cualquier función que necesites va aquí. Se llaman desde el HTML con `@click`, `@submit`, etc.

```javascript
methods: {
    async loadUsers() {
        this.users = await Api.get('/usuarios');
    },
    async save() {
        await Api.post('/usuarios', this.form);
        await this.loadUsers();  // refrescar la tabla
    }
}
```

### computed — Valores calculados

Son datos que se derivan de otros datos. Se recalculan automáticamente.

```javascript
computed: {
    // Filtrar usuarios por búsqueda
    filteredUsers() {
        if (!this.search) return this.users;
        return this.users.filter(u =>
            u.nombre.toLowerCase().includes(this.search.toLowerCase())
        );
    },
    // Sumar porcentajes
    totalPorcentaje() {
        return this.cortes.reduce((sum, c) => sum + c.porcentaje, 0);
    }
}
```

---

## Vue — Directivas en el HTML

Las directivas son atributos especiales que Vue entiende. Estas son las que usamos:

### v-for — Repetir elementos (listas y tablas)

```html
<!-- Repetir una fila por cada usuario -->
<tr v-for="user in users" :key="user.id">
    <td>{{ user.nombre }}</td>
    <td>{{ user.correo }}</td>
</tr>

<!-- Con índice (para "Corte 1", "Corte 2"...) -->
<tr v-for="(corte, index) in cortes" :key="corte.id">
    <td>Corte {{ index + 1 }}</td>
</tr>
```

> **Importante:** siempre agregar `:key="algo_unico"` — Vue lo necesita para rastrear cada elemento.

### v-model — Conectar input ↔ dato

Lo que el usuario escribe en el input se guarda automáticamente en la variable, y viceversa.

```html
<!-- El input y form.nombre están sincronizados -->
<input type="text" v-model="form.nombre">

<!-- También funciona con selects -->
<select v-model="form.rol">
    <option value="admin">Admin</option>
    <option value="profesor">Profesor</option>
</select>
```

### v-if / v-else — Mostrar u ocultar

```html
<!-- Mostrar solo si hay error -->
<div v-if="error" class="alert alert-danger">{{ error }}</div>

<!-- Mostrar una cosa u otra -->
<span v-if="user.activo" class="badge bg-success">Activo</span>
<span v-else class="badge bg-danger">Inactivo</span>

<!-- Mostrar sección solo cuando se selecciona un curso -->
<div v-if="selectedCurso">
    <!-- tabla de cortes -->
</div>
<div v-else>
    <p>Selecciona un curso</p>
</div>
```

### {{ }} — Mostrar datos en el HTML

```html
<td>{{ user.nombre }}</td>                    <!-- texto simple -->
<td>{{ user.activo ? 'Sí' : 'No' }}</td>    <!-- con condición -->
<td>{{ corte.porcentaje }}%</td>              <!-- concatenar texto -->
```

### @ — Eventos (click, submit, change)

```html
<button @click="save">Guardar</button>               <!-- click -->
<form @submit.prevent="save">                          <!-- submit sin recargar -->
<select @change="loadCortes">                          <!-- cuando cambia el valor -->
```

> `@submit.prevent` es como hacer `event.preventDefault()` — evita que el formulario recargue la página.

### : — Atributos dinámicos

Los dos puntos antes de un atributo hacen que Vue evalúe su valor como JavaScript.

```html
<!-- Sin : → texto literal "saving" -->
<button disabled="saving">

<!-- Con : → evalúa la variable saving (true/false) -->
<button :disabled="saving">

<!-- Clases dinámicas -->
<span :class="activo ? 'text-success' : 'text-danger'">
```

---

## Bootstrap — Lo que más van a usar

### Tablas

```html
<div class="card shadow-sm">
    <div class="table-responsive">
        <table class="table table-hover mb-0">
            <thead class="table-light">
                <tr>
                    <th>Nombre</th>
                    <th>Correo</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="u in users" :key="u.id">
                    <td>{{ u.nombre }}</td>
                    <td>{{ u.correo }}</td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
```

### Modales (ventanas emergentes)

```html
<!-- Botón que abre el modal -->
<button @click="openModal">Crear</button>

<!-- El modal -->
<div class="modal fade" id="miModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Titulo</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form @submit.prevent="save">
                <div class="modal-body">
                    <!-- campos del formulario -->
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Guardar</button>
                </div>
            </form>
        </div>
    </div>
</div>
```

**Abrir modal desde JavaScript:**

```javascript
new bootstrap.Modal(document.getElementById('miModal')).show();
```

**Cerrar modal desde JavaScript:**

```javascript
bootstrap.Modal.getInstance(document.getElementById('miModal')).hide();
```

### Alertas

```html
<div class="alert alert-danger py-2">Mensaje de error</div>
<div class="alert alert-success py-2">Mensaje de éxito</div>
<div class="alert alert-warning py-2">Mensaje de advertencia</div>
```

### Badges (etiquetas de colores)

```html
<span class="badge bg-success-subtle text-success">Activo</span>
<span class="badge bg-danger-subtle text-danger">Inactivo</span>
<span class="badge bg-primary-subtle text-primary">Profesor</span>
```

### Botones

```html
<button class="btn btn-primary">Azul (acción principal)</button>
<button class="btn btn-danger">Rojo (eliminar)</button>
<button class="btn btn-outline-primary btn-sm">Azul outline pequeño</button>
<button class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
```

### Spinner de carga en botón

```html
<button class="btn btn-primary" :disabled="saving">
    <span v-if="saving" class="spinner-border spinner-border-sm me-1"></span>
    {{ saving ? 'Guardando...' : 'Guardar' }}
</button>
```

---

## api.js — Cómo hablar con el backend

El módulo `api.js` ya está hecho. Solo necesitan llamar estos métodos:

```javascript
// Leer datos
const usuarios = await Api.get('/usuarios');
const curso = await Api.get('/cursos/1');

// Crear algo nuevo
await Api.post('/cursos', { nombre: 'POO', codigo: 'POO-401', profesorId: 2, semestreId: 1 });

// Editar algo existente
await Api.put('/usuarios/1', { nombre: 'Nuevo Nombre', correo: 'nuevo@correo.com', rol: 'admin' });

// Eliminar algo
await Api.delete('/usuarios/1');
```

**Importante:**
- El token se agrega automáticamente — no tienen que preocuparse por eso
- Si el servidor responde 401 (sesión expirada), redirige al login automáticamente
- Si el servidor responde con error, se lanza una excepción — usar try/catch

### Patrón try/catch para todas las peticiones

```javascript
// Para cargar datos (mostrar toast de error)
async loadData() {
    try {
        this.items = await Api.get('/endpoint');
    } catch (e) {
        Utils.toast('Error al cargar datos', 'danger');
    }
}

// Para guardar datos (mostrar error en el modal)
async save() {
    this.formError = '';
    this.saving = true;
    try {
        await Api.post('/endpoint', this.form);
        Utils.toast('Creado exitosamente');
        bootstrap.Modal.getInstance(document.getElementById('miModal')).hide();
        await this.loadData();  // refrescar la tabla
    } catch (e) {
        this.formError = e.message;  // mostrar el error del servidor en el modal
    } finally {
        this.saving = false;
    }
}
```

---

## Cargar datos de múltiples endpoints al mismo tiempo

Cuando necesitas datos de varias fuentes (ej: cursos + matrículas + estudiantes), usa `Promise.all` para cargarlos en paralelo:

```javascript
// ❌ Lento — uno después del otro
const cursos = await Api.get('/cursos');
const usuarios = await Api.get('/usuarios');
const semestres = await Api.get('/semestres');

// ✅ Rápido — todos al mismo tiempo
const [cursos, usuarios, semestres] = await Promise.all([
    Api.get('/cursos'),
    Api.get('/usuarios'),
    Api.get('/semestres')
]);
```

---

## Selects dinámicos — Cargar opciones desde la API

Cuando un formulario tiene un select que muestra datos del backend (ej: lista de profesores):

```javascript
data() {
    return {
        profesores: [],
        form: { profesorId: '' }
    }
},
mounted() {
    this.loadProfesores();
},
methods: {
    async loadProfesores() {
        const usuarios = await Api.get('/usuarios');
        this.profesores = usuarios.filter(u => u.rol === 'profesor' && u.activo);
    }
}
```

```html
<select class="form-select" v-model="form.profesorId" required>
    <option value="">Seleccionar profesor</option>
    <option v-for="p in profesores" :key="p.id" :value="p.id">
        {{ p.nombre }}
    </option>
</select>
```

> **Importante:** los selects retornan strings. Antes de enviar al backend, convertir con `Number()`:
> ```javascript
> profesorId: Number(this.form.profesorId)
> ```

---

## Componente NavBar

El navbar es un componente Vue reutilizable. Se usa así en cada página:

**En el HTML:**
```html
<nav-bar active="usuarios"></nav-bar>
```

**En el script (antes de .mount):**
```javascript
Vue.createApp({ ... })
    .component('nav-bar', NavBar)
    .mount('#app');
```

El prop `active` indica cuál link resaltar. Debe coincidir con el `id` del link en `components.js`:
- Admin: `usuarios`, `semestres`, `cursos`, `matriculas`
- Profesor: `mis-cursos`, `actividades`, `notas`
- Estudiante: `mis-notas`

---

## Flujo completo: crear un registro paso a paso

Este es el flujo cuando el usuario hace click en "Nuevo" → llena el formulario → click "Guardar":

```
1. Click en botón "Nuevo"
   → openCreate() se ejecuta
   → limpia el formulario: this.form = { nombre: '', correo: '' }
   → abre el modal: new bootstrap.Modal(...).show()

2. Usuario llena los campos
   → v-model actualiza this.form automáticamente

3. Click en "Guardar" (submit del form)
   → @submit.prevent="save" llama save()
   → save() pone saving = true (botón muestra spinner)
   → Api.post('/endpoint', this.form) envía al backend

4a. Si el backend responde OK:
    → Utils.toast('Creado') muestra mensaje verde
    → Modal se cierra
    → loadData() refresca la tabla

4b. Si el backend responde con error:
    → catch captura el error
    → this.formError = e.message muestra el mensaje en el modal

5. finally pone saving = false (botón vuelve a la normalidad)
```

---

## Errores comunes

| Error | Causa | Solución |
|-------|-------|----------|
| La tabla no muestra datos | Falta `await` en el `Api.get()` | Agregar `await` y hacer el método `async` |
| El modal no abre | El id del modal no coincide | Verificar que el id en el HTML y en el JS sean iguales |
| Error 401 al cargar | La sesión expiró | Hacer login de nuevo |
| Error 403 al crear | El rol no tiene permiso | Verificar el guard y el rol del usuario logueado |
| El select manda string en vez de número | Los selects de HTML retornan strings | Usar `Number(this.form.profesorId)` al enviar |
| "Cannot read properties of null" | El elemento no existe cuando se busca | Verificar que el id existe y que Vue ya montó el componente |
| Los cambios no se ven en la tabla | No se llamó `loadData()` después de crear/editar | Agregar `await this.loadData()` al final del try |

---

## Cómo probar tu página

1. Asegurarse de que el backend está corriendo en `http://localhost:8080`
2. Abrir la carpeta `frontend/` con VS Code
3. Click derecho en tu archivo `.html` → "Open with Live Server"
4. Hacer login con el usuario de prueba correspondiente:
   - Admin: `admin@notas.com` / `1234`
   - Profesor: `carlos@notas.com` / `1234`
   - Estudiante: `juan@notas.com` / `1234`
5. Probar: crear, editar, eliminar — verificar que la tabla se actualiza
6. Abrir la consola del navegador (F12) para ver errores si algo no funciona
