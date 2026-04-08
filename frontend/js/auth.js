// ============================================================
// auth.js - Manejo de autenticacion en el frontend
// Importar en todas las paginas del proyecto
// ============================================================

const Auth = {

    // Ejecuta el login contra la API
    // Guarda token y datos del usuario en sessionStorage
    login: async function(correo, password) {
        const data = await Api.post('/auth/login', { correo, password });

        if (data && data.token) {
            sessionStorage.setItem('token', data.token);
            sessionStorage.setItem('usuario', JSON.stringify({
                id: data.id,
                nombre: data.nombre,
                correo: data.correo,
                rol: data.rol
            }));
            return data;
        }

        return null;
    },

    // Cierra sesion y redirige al login
    logout: async function() {
        try {
            await Api.post('/auth/logout', {});
        } catch (e) {
            // Si falla el logout en el server, igual limpiamos el frontend
        }
        sessionStorage.clear();
        window.location.href = '/index.html';
    },

    // Retorna el usuario actual o null si no hay sesion
    getUsuario: function() {
        const data = sessionStorage.getItem('usuario');
        if (data) {
            return JSON.parse(data);
        }
        return null;
    },

    // Retorna el rol del usuario actual
    getRol: function() {
        const usuario = this.getUsuario();
        return usuario ? usuario.rol : null;
    },

    // Verifica si hay sesion activa
    isLoggedIn: function() {
        return sessionStorage.getItem('token') !== null;
    },

    // Guard: redirige al login si no hay sesion
    // Llamar al inicio de cada pagina protegida
    requireAuth: function() {
        if (!this.isLoggedIn()) {
            window.location.href = '/index.html';
            return false;
        }
        return true;
    },

    // Guard: redirige si el usuario no tiene el rol requerido
    // Ejemplo: Auth.requireRol('admin')
    requireRol: function(rol) {
        if (!this.requireAuth()) return false;

        if (this.getRol() !== rol) {
            window.location.href = '/pages/dashboard.html';
            return false;
        }
        return true;
    },

    // Redirige al dashboard segun el rol del usuario
    redirectByRol: function() {
        window.location.href = '/pages/dashboard.html';
    }
};
