// components.js - Componentes Vue reutilizables
// Importar en todas las paginas DESPUES de vue.global.js, api.js y auth.js

const FooterBar = {
    template: `
    <footer class="footer-bar">
        <div class="container d-flex justify-content-between align-items-center flex-wrap gap-2">
            <div class="d-flex align-items-center gap-3">
                <span class="footer-title">Sistema de Notas - Ingeniería de Software II</span>
                <a href="https://github.com/david-fb/SistemaNotas-U" target="_blank" class="footer-github" title="Ver en GitHub">
                    <i class="bi bi-github"></i>
                </a>
            </div>
            <div class="dropup">
                <button class="btn btn-footer dropdown-toggle" type="button" data-bs-toggle="dropdown">
                    Integrantes
                </button>
                <ul class="dropdown-menu dropdown-menu-end">
                    <li><span class="dropdown-item-text">Alejandro Alisajar</span></li>
                    <li><span class="dropdown-item-text">Jhon Angulo González</span></li>
                    <li><span class="dropdown-item-text">David Basto Martínez</span></li>
                    <li><span class="dropdown-item-text">Jimmi Calvo Hoyos</span></li>
                </ul>
            </div>
        </div>
    </footer>`
};

const NavBar = {
    props: ['active'],
    template: `
    <nav class="navbar navbar-expand-lg navbar-brand-custom shadow-sm mb-4">
        <div class="container">
            <a class="navbar-brand fw-bold d-flex align-items-center gap-2" href="/pages/dashboard.html">
                <i class="bi bi-mortarboard-fill"></i> Sistema de Notas
            </a>
            <button class="navbar-toggler border-0" type="button" data-bs-toggle="collapse" data-bs-target="#navMenu" style="color:rgba(255,255,255,0.8)">
                <i class="bi bi-list fs-4"></i>
            </button>
            <div class="collapse navbar-collapse" id="navMenu">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-3">
                    <li class="nav-item" v-for="link in links" :key="link.id">
                        <a class="nav-link nav-link-custom" :class="{ 'active-link': active === link.id }" :href="link.href">
                            {{ link.text }}
                        </a>
                    </li>
                </ul>
                <div class="d-flex align-items-center gap-2 gap-md-3 mt-2 mt-lg-0">
                    <div class="nav-avatar">{{ iniciales }}</div>
                    <div class="d-none d-md-flex flex-column" style="line-height:1.2">
                        <span class="nav-username">{{ usuario.nombre }}</span>
                        <span style="font-size:0.72rem; color:rgba(255,255,255,0.5);">{{ usuario.rol }}</span>
                    </div>
                    <button class="btn btn-nav-logout btn-sm d-flex align-items-center gap-1" @click="logout">
                        <i class="bi bi-box-arrow-right"></i>
                        <span class="d-none d-md-inline">Cerrar Sesión</span>
                    </button>
                </div>
            </div>
        </div>
    </nav>`,
    data() {
        return { usuario: Auth.getUsuario() || { nombre: '', rol: '' } }
    },
    computed: {
        iniciales() {
            return this.usuario.nombre.split(' ').slice(0,2).map(p => p[0]).join('').toUpperCase();
        },
        links() {
            const menus = {
                admin: [
                    { id: 'usuarios',   text: 'Usuarios',   href: '/pages/admin/usuarios.html' },
                    { id: 'semestres',  text: 'Semestres',  href: '/pages/admin/semestres.html' },
                    { id: 'cursos',     text: 'Cursos',     href: '/pages/admin/cursos.html' },
                    { id: 'matriculas', text: 'Matrículas', href: '/pages/admin/matriculas.html' }
                ],
                profesor: [
                    { id: 'mis-cursos',   text: 'Mis Cursos',   href: '/pages/profesor/cursos.html' },
                    { id: 'actividades',  text: 'Actividades',  href: '/pages/profesor/actividades.html' },
                    { id: 'notas',        text: 'Notas',        href: '/pages/profesor/notas.html' }
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
    },
    methods: {
        logout() { Auth.logout(); }
    }
};
