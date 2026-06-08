// components.js - Componentes Vue reutilizables
// Importar en todas las paginas DESPUES de vue.global.js, api.js y auth.js

const NavBar = {
    props: ['active'],
    template: `
    <nav class="navbar navbar-expand-lg bg-white border-bottom shadow-sm mb-4">
        <div class="container">
            <a class="navbar-brand fw-bold text-primary" href="/pages/dashboard.html">Sistema de Notas</a>
            <div class="d-flex align-items-center gap-3">
                <a v-for="link in links" :key="link.id"
                   :href="link.href"
                   class="nav-link"
                   :class="{ 'active fw-semibold': active === link.id }">
                    {{ link.text }}
                </a>
                <span class="text-muted small d-none d-md-inline">{{ usuario.nombre }}</span>
                <span class="badge" :class="badgeClass">{{ usuario.rol }}</span>
                <button class="btn btn-outline-secondary btn-sm" @click="Auth.logout()">Cerrar Sesion</button>
            </div>
        </div>
    </nav>`,
    data() {
        return { usuario: Auth.getUsuario() || { nombre: '', rol: '' } }
    },
    computed: {
        links() {
            const menus = {
                admin: [
                    { id: 'usuarios', text: 'Usuarios', href: '/pages/admin/usuarios.html' },
                    { id: 'semestres', text: 'Semestres', href: '/pages/admin/semestres.html' },
                    { id: 'cursos', text: 'Cursos', href: '/pages/admin/cursos.html' },
                    { id: 'matriculas', text: 'Matriculas', href: '/pages/admin/matriculas.html' }
                ],
                profesor: [
                    { id: 'mis-cursos', text: 'Mis Cursos', href: '/pages/profesor/cursos.html' },
                    { id: 'actividades', text: 'Actividades', href: '/pages/profesor/actividades.html' },
                    { id: 'notas', text: 'Notas', href: '/pages/profesor/notas.html' }
                ],
                estudiante: [
                    { id: 'mis-notas', text: 'Mis Notas', href: '/pages/estudiante/mis-notas.html' }
                ]
            };
            return menus[this.usuario.rol] || [];
        },
        badgeClass() {
            const map = { admin: 'bg-purple-subtle text-purple', profesor: 'bg-primary-subtle text-primary', estudiante: 'bg-success-subtle text-success' };
            return map[this.usuario.rol] || '';
        }
    }
};
