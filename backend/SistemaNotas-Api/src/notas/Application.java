package notas;

import com.sun.net.httpserver.HttpServer;

import controller.AuthController;
import controller.UsuarioController;
import middleware.AuthMiddleware;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Application {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // ============================================================
        // Rutas publicas (no requieren autenticacion)
        // ============================================================
        server.createContext("/api/auth", new AuthController());

        // ============================================================
        // Rutas protegidas (requieren token valido)
        // Cada ruta pasa por el AuthMiddleware antes de llegar al controller
        // ============================================================
        // Descomenta cada ruta cuando termines su controller
        server.createContext("/api/usuarios", new AuthMiddleware(new UsuarioController()));
        // server.createContext("/api/semestres",  new AuthMiddleware(new SemestreController()));
        // server.createContext("/api/cursos",     new AuthMiddleware(new CursoController()));
        // server.createContext("/api/matriculas", new AuthMiddleware(new MatriculaController()));
        // server.createContext("/api/cortes",     new AuthMiddleware(new CorteController()));
        // server.createContext("/api/notas",      new AuthMiddleware(new NotaController()));

        server.setExecutor(null);
        server.start();

        System.out.println("=================================");
        System.out.println("  API Sistema de Notas iniciada");
        System.out.println("  http://localhost:" + PORT);
        System.out.println("=================================");
    }
}
