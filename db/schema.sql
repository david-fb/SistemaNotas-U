-- ============================================================
-- Sistema de Notas - Schema de Base de Datos
-- PostgreSQL
-- ============================================================

-- Eliminar tablas si existen (en orden por dependencias)
DROP TABLE IF EXISTS nota;
DROP TABLE IF EXISTS actividad;
DROP TABLE IF EXISTS curso_estudiante;
DROP TABLE IF EXISTS curso;
DROP TABLE IF EXISTS usuario;

-- ============================================================
-- USUARIO
-- Roles: 'admin', 'profesor', 'estudiante'
-- Password se almacena con md5() de PostgreSQL
-- ============================================================
CREATE TABLE usuario (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    correo      VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    rol         VARCHAR(20)  NOT NULL CHECK (rol IN ('admin', 'profesor', 'estudiante')),
    activo      BOOLEAN      NOT NULL DEFAULT TRUE
);

-- ============================================================
-- CURSO
-- Cada curso pertenece a un profesor
-- ============================================================
CREATE TABLE curso (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    codigo      VARCHAR(20)  NOT NULL UNIQUE,
    profesor_id INTEGER      NOT NULL REFERENCES usuario(id)
);

-- ============================================================
-- CURSO_ESTUDIANTE
-- Relacion muchos a muchos entre curso y usuario (estudiante)
-- ============================================================
CREATE TABLE curso_estudiante (
    id              SERIAL PRIMARY KEY,
    curso_id        INTEGER NOT NULL REFERENCES curso(id) ON DELETE CASCADE,
    estudiante_id   INTEGER NOT NULL REFERENCES usuario(id),
    UNIQUE(curso_id, estudiante_id)
);

-- ============================================================
-- ACTIVIDAD
-- Actividades evaluativas de un curso (parcial, taller, quiz)
-- La suma de porcentajes por curso debe ser 100%
-- ============================================================
CREATE TABLE actividad (
    id          SERIAL PRIMARY KEY,
    curso_id    INTEGER      NOT NULL REFERENCES curso(id) ON DELETE CASCADE,
    nombre      VARCHAR(100) NOT NULL,
    tipo        VARCHAR(20)  NOT NULL CHECK (tipo IN ('parcial', 'taller', 'quiz')),
    porcentaje  DECIMAL(5,2) NOT NULL CHECK (porcentaje > 0 AND porcentaje <= 100)
);

-- ============================================================
-- NOTA
-- Calificacion de un estudiante en una actividad
-- Valor entre 0.0 y 5.0
-- ============================================================
CREATE TABLE nota (
    id              SERIAL PRIMARY KEY,
    estudiante_id   INTEGER      NOT NULL REFERENCES usuario(id),
    actividad_id    INTEGER      NOT NULL REFERENCES actividad(id) ON DELETE CASCADE,
    valor           DECIMAL(3,1) NOT NULL CHECK (valor >= 0.0 AND valor <= 5.0),
    UNIQUE(estudiante_id, actividad_id)
);