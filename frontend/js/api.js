// ============================================================
// api.js - Modulo centralizado de peticiones al backend
// Todos los fetch() del proyecto pasan por aqui
// El token se agrega automaticamente a cada peticion
// ============================================================

const API_BASE = 'http://localhost:8080/api';

const Api = {

    // GET request
    get: async function(endpoint) {
        return await this.request(endpoint, 'GET');
    },

    // POST request con body JSON
    post: async function(endpoint, data) {
        return await this.request(endpoint, 'POST', data);
    },

    // PUT request con body JSON
    put: async function(endpoint, data) {
        return await this.request(endpoint, 'PUT', data);
    },

    // DELETE request
    delete: async function(endpoint) {
        return await this.request(endpoint, 'DELETE');
    },

    // Metodo base que ejecuta todas las peticiones
    request: async function(endpoint, method, data = null) {
        const url = API_BASE + endpoint;

        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            }
        };

        // Agregar token si existe en sessionStorage
        const token = sessionStorage.getItem('token');
        if (token) {
            options.headers['Authorization'] = 'Bearer ' + token;
        }

        // Agregar body si hay datos
        if (data) {
            options.body = JSON.stringify(data);
        }

        try {
            const response = await fetch(url, options);

            // Si el servidor responde 401, redirigir al login
            if (response.status === 401) {
                sessionStorage.clear();
                window.location.href = '/index.html';
                return null;
            }

            const json = await response.json();

            // Si hay error, lanzar excepcion con el mensaje del servidor
            if (!response.ok) {
                throw new Error(json.error || 'Error en la peticion');
            }

            return json;

        } catch (error) {
            console.error('Error en ' + method + ' ' + endpoint + ':', error.message);
            throw error;
        }
    }
};
