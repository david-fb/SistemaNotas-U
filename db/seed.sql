-- ============================================================
-- Sistema de Notas - Datos de Prueba
-- Ejecutar despues de schema.sql
-- Passwords: todas son "1234" cifradas con md5()
-- ============================================================

-- ============================================================
-- USUARIOS
-- ============================================================

-- Admin
INSERT INTO usuario (nombre, correo, password, rol, activo) VALUES
('Administrador', 'admin@notas.com', md5('1234'), 'admin', true);

-- Profesores
INSERT INTO usuario (nombre, correo, password, rol, activo) VALUES
('Carlos Ramirez', 'carlos@notas.com', md5('1234'), 'profesor', true),
('Laura Martinez', 'laura@notas.com', md5('1234'), 'profesor', true);

-- Estudiantes
INSERT INTO usuario (nombre, correo, password, rol, activo) VALUES
('Juan Perez', 'juan@notas.com', md5('1234'), 'estudiante', true),
('Maria Garcia', 'maria@notas.com', md5('1234'), 'estudiante', true),
('Andres Lopez', 'andres@notas.com', md5('1234'), 'estudiante', true),
('Sofia Torres', 'sofia@notas.com', md5('1234'), 'estudiante', true),
('Daniel Castro', 'daniel@notas.com', md5('1234'), 'estudiante', true);

-- ============================================================
-- CURSOS
-- Carlos (id=2) dicta Programacion y Bases de Datos
-- Laura (id=3) dicta Calculo
-- ============================================================
INSERT INTO curso (nombre, codigo, profesor_id) VALUES
('Programacion Orientada a Objetos', 'POO-401', 2),
('Bases de Datos', 'BD-402', 2),
('Calculo Diferencial', 'CAL-403', 3);

-- ============================================================
-- MATRICULAS
-- Juan, Maria y Andres en POO
-- Juan, Sofia y Daniel en Bases de Datos
-- Maria, Andres, Sofia y Daniel en Calculo
-- ============================================================
INSERT INTO curso_estudiante (curso_id, estudiante_id) VALUES
(1, 4), (1, 5), (1, 6),
(2, 4), (2, 7), (2, 8),
(3, 5), (3, 6), (3, 7), (3, 8);

-- ============================================================
-- ACTIVIDADES EVALUATIVAS
-- POO: parcial 30%, taller 30%, quiz 20%, parcial final 20% = 100%
-- Bases de Datos: parcial 35%, taller 25%, quiz 15%, parcial final 25% = 100%
-- Calculo: parcial 40%, taller 20%, quiz 10%, parcial final 30% = 100%
-- ============================================================
INSERT INTO actividad (curso_id, nombre, tipo, porcentaje) VALUES
(1, 'Primer Parcial', 'parcial', 30.00),
(1, 'Taller Herencia', 'taller', 30.00),
(1, 'Quiz Polimorfismo', 'quiz', 20.00),
(1, 'Parcial Final', 'parcial', 20.00),

(2, 'Primer Parcial', 'parcial', 35.00),
(2, 'Taller SQL', 'taller', 25.00),
(2, 'Quiz Normalizacion', 'quiz', 15.00),
(2, 'Parcial Final', 'parcial', 25.00),

(3, 'Primer Parcial', 'parcial', 40.00),
(3, 'Taller Derivadas', 'taller', 20.00),
(3, 'Quiz Limites', 'quiz', 10.00),
(3, 'Parcial Final', 'parcial', 30.00);

-- ============================================================
-- NOTAS
-- Algunas notas de ejemplo para probar calculos
-- POO (actividades 1-4): Juan(4), Maria(5), Andres(6)
-- ============================================================
INSERT INTO nota (estudiante_id, actividad_id, valor) VALUES
(4, 1, 4.2), (4, 2, 3.8), (4, 3, 4.5), (4, 4, 3.9),
(5, 1, 3.5), (5, 2, 4.0), (5, 3, 3.0), (5, 4, 4.2),
(6, 1, 2.8), (6, 2, 3.2), (6, 3, 2.5), (6, 4, 3.0);

-- Bases de Datos (actividades 5-8): Juan(4), Sofia(7), Daniel(8)
INSERT INTO nota (estudiante_id, actividad_id, valor) VALUES
(4, 5, 4.0), (4, 6, 4.5), (4, 7, 3.8), (4, 8, 4.1),
(7, 5, 3.2), (7, 6, 3.5), (7, 7, 4.0), (7, 8, 3.0),
(8, 5, 2.5), (8, 6, 2.8), (8, 7, 3.0), (8, 8, 2.2);

-- Calculo (actividades 9-12): Maria(5), Andres(6), Sofia(7), Daniel(8)
INSERT INTO nota (estudiante_id, actividad_id, valor) VALUES
(5, 9, 3.8), (5, 10, 4.0), (5, 11, 3.5), (5, 12, 4.2),
(6, 9, 2.5), (6, 10, 3.0), (6, 11, 2.0), (6, 12, 2.8),
(7, 9, 4.5), (7, 10, 4.2), (7, 11, 5.0), (7, 12, 4.8),
(8, 9, 3.0), (8, 10, 3.5), (8, 11, 3.2), (8, 12, 2.9);