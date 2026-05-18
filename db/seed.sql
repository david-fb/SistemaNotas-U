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
('Juan Perez',    'juan@notas.com',   md5('1234'), 'estudiante', true),
('Maria Garcia',  'maria@notas.com',  md5('1234'), 'estudiante', true),
('Andres Lopez',  'andres@notas.com', md5('1234'), 'estudiante', true),
('Sofia Torres',  'sofia@notas.com',  md5('1234'), 'estudiante', true),
('Daniel Castro', 'daniel@notas.com', md5('1234'), 'estudiante', true);

-- ============================================================
-- SEMESTRE
-- ============================================================
INSERT INTO semestre (nombre, fecha_inicio, fecha_fin) VALUES
('2025-1', '2025-01-20', '2025-06-15');

-- ============================================================
-- CURSOS
-- semestre_id=1 para todos
-- Carlos (id=2) dicta POO y Bases de Datos
-- Laura (id=3) dicta Calculo
-- ============================================================
INSERT INTO curso (nombre, codigo, profesor_id, semestre_id) VALUES
('Programacion Orientada a Objetos', 'POO-401', 2, 1),
('Bases de Datos',                   'BD-402',  2, 1),
('Calculo Diferencial',              'CAL-403', 3, 1);

-- ============================================================
-- MATRICULAS
-- Juan, Maria y Andres en POO
-- Juan, Sofia y Daniel en Bases de Datos
-- Maria, Andres, Sofia y Daniel en Calculo
-- ============================================================
INSERT INTO matricula (curso_id, estudiante_id, fecha, estado) VALUES
(1, 4, '2025-01-20', true),
(1, 5, '2025-01-20', true),
(1, 6, '2025-01-20', true),
(2, 4, '2025-01-20', true),
(2, 7, '2025-01-20', true),
(2, 8, '2025-01-20', true),
(3, 5, '2025-01-20', true),
(3, 6, '2025-01-20', true),
(3, 7, '2025-01-20', true),
(3, 8, '2025-01-20', true);

-- ============================================================
-- CORTES
-- POO:            30% + 30% + 20% + 20% = 100%
-- Bases de Datos: 35% + 25% + 15% + 25% = 100%
-- Calculo:        40% + 20% + 10% + 30% = 100%
-- ============================================================
INSERT INTO corte (curso_id, porcentaje) VALUES
(1, 30.00),
(1, 30.00),
(1, 20.00),
(1, 20.00),

(2, 35.00),
(2, 25.00),
(2, 15.00),
(2, 25.00),

(3, 40.00),
(3, 20.00),
(3, 10.00),
(3, 30.00);

-- ============================================================
-- NOTAS
-- POO (cortes 1-4): Juan(4), Maria(5), Andres(6)
-- ============================================================
INSERT INTO nota (estudiante_id, corte_id, valor) VALUES
(4, 1, 4.2), (4, 2, 3.8), (4, 3, 4.5), (4, 4, 3.9),
(5, 1, 3.5), (5, 2, 4.0), (5, 3, 3.0), (5, 4, 4.2),
(6, 1, 2.8), (6, 2, 3.2), (6, 3, 2.5), (6, 4, 3.0);

-- Bases de Datos (cortes 5-8): Juan(4), Sofia(7), Daniel(8)
INSERT INTO nota (estudiante_id, corte_id, valor) VALUES
(4, 5, 4.0), (4, 6, 4.5), (4, 7, 3.8), (4, 8, 4.1),
(7, 5, 3.2), (7, 6, 3.5), (7, 7, 4.0), (7, 8, 3.0),
(8, 5, 2.5), (8, 6, 2.8), (8, 7, 3.0), (8, 8, 2.2);

-- Calculo (cortes 9-12): Maria(5), Andres(6), Sofia(7), Daniel(8)
INSERT INTO nota (estudiante_id, corte_id, valor) VALUES
(5, 9,  3.8), (5, 10, 4.0), (5, 11, 3.5), (5, 12, 4.2),
(6, 9,  2.5), (6, 10, 3.0), (6, 11, 2.0), (6, 12, 2.8),
(7, 9,  4.5), (7, 10, 4.2), (7, 11, 5.0), (7, 12, 4.8),
(8, 9,  3.0), (8, 10, 3.5), (8, 11, 3.2), (8, 12, 2.9);
