// utils.js - Helpers compartidos para todas las páginas

const Utils = {

    // Muestra un toast de Bootstrap
    toast(message, type = 'success') {
        const container = document.getElementById('toast-container') || Utils.createToastContainer();
        const id = 'toast-' + Date.now();
        const bgClass = type === 'success' ? 'text-bg-success' : type === 'danger' ? 'text-bg-danger' : 'text-bg-warning';
        container.innerHTML += `
            <div id="${id}" class="toast align-items-center ${bgClass} border-0" role="alert">
                <div class="d-flex">
                    <div class="toast-body">${message}</div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>`;
        const toast = new bootstrap.Toast(document.getElementById(id), { delay: 3000 });
        toast.show();
    },

    createToastContainer() {
        const div = document.createElement('div');
        div.id = 'toast-container';
        div.className = 'toast-container position-fixed top-0 end-0 p-3';
        div.style.zIndex = '1080';
        document.body.appendChild(div);
        return div;
    },

    // Confirmación antes de eliminar
    async confirm(message = '¿Estas seguro?') {
        return window.confirm(message);
    },

    // Formatea fecha ISO a dd/mm/yyyy
    formatDate(dateStr) {
        if (!dateStr) return '-';
        const parts = dateStr.split('-');
        if (parts.length === 3) return `${parts[2]}/${parts[1]}/${parts[0]}`;
        return dateStr;
    },

    // Genera el navbar HTML reutilizable
    navbar(links = []) {
        const usuario = Auth.getUsuario();
        const nav = links.map(l => `<a href="${l.href}" class="nav-link${l.active ? ' active fw-semibold' : ''}">${l.text}</a>`).join('');
        return `
        <nav class="navbar navbar-expand-lg bg-white border-bottom shadow-sm mb-4">
            <div class="container">
                <a class="navbar-brand fw-bold text-primary" href="/pages/dashboard.html">Sistema de Notas</a>
                <div class="d-flex align-items-center gap-3">
                    ${nav ? '<div class="d-flex gap-3">' + nav + '</div>' : ''}
                    <span class="text-muted small">${usuario ? usuario.nombre : ''}</span>
                    <span class="badge bg-${usuario?.rol === 'admin' ? 'purple' : usuario?.rol === 'profesor' ? 'primary' : 'success'}-subtle text-${usuario?.rol === 'admin' ? 'purple' : usuario?.rol === 'profesor' ? 'primary' : 'success'}">${usuario?.rol || ''}</span>
                    <button class="btn btn-outline-secondary btn-sm" onclick="Auth.logout()">Cerrar Sesion</button>
                </div>
            </div>
        </nav>`;
    }
};
