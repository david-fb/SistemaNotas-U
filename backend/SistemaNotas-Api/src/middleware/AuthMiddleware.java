package middleware;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import model.Usuario;
import util.HttpHelper;
import util.SessionManager;

import java.io.IOException;

public class AuthMiddleware implements HttpHandler {

    private final HttpHandler next;

    // Recibe el controller real como parametro
    // Ejemplo: new AuthMiddleware(new UsuarioController())
    public AuthMiddleware(HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Dejar pasar las peticiones OPTIONS (preflight de CORS)
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            HttpHelper.handleCors(exchange);
            return;
        }

        // Extraer el token del header Authorization
        String token = HttpHelper.getToken(exchange);

        // Validar que el token existe y es valido
        if (token == null || !SessionManager.isValid(token)) {
            HttpHelper.sendError(exchange, 401, "No autorizado. Inicia sesion primero.");
            return;
        }

        // Obtener el usuario de la sesion y guardarlo en los atributos del exchange
        // para que el controller pueda acceder a el
        Usuario usuario = SessionManager.getUsuario(token);
        exchange.setAttribute("usuario", usuario);

        // Si todo esta bien, pasar la peticion al controller real
        next.handle(exchange);
    }
}