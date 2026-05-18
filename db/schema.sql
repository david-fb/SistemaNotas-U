-- ============================================================
-- Sistema de Notas - Schema de Base de Datos
-- PostgreSQL
-- ============================================================

-- Eliminar tablas si existen (en orden por dependencias)
DROP TABLE IF EXISTS nota;
DROP TABLE IF EXISTS corte;
DROP TABLE IF EXISTS matricula;
DROP TABLE IF EXISTS curso;
DROP TABLE IF EXISTS semestre;
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
-- SEMESTRE
-- Periodo academico al que pertenecen los cursos
-- ============================================================
CREATE TABLE semestre (
    id           SERIAL PRIMARY KEY,
    nombre       VARCHAR(50)  NOT NULL,
    fecha_inicio VARCHAR(20)  NOT NULL,
    fecha_fin    VARCHAR(20)  NOT NULL
);

-- ============================================================
-- CURSO
-- Cada curso pertenece a un profesor y a un semestre
-- ============================================================
CREATE TABLE curso (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    codigo      VARCHAR(20)  NOT NULL UNIQUE,
    profesor_id INTEGER      NOT NULL REFERENCES usuario(id),
    semestre_id INTEGER      NOT NULL REFERENCES semestre(id)
);

-- ============================================================
-- MATRICULA
-- Inscripcion de un estudiante en un curso
-- ============================================================
CREATE TABLE matricula (
    id            SERIAL PRIMARY KEY,
    curso_id      INTEGER      NOT NULL REFERENCES curso(id) ON DELETE CASCADE,
    estudiante_id INTEGER      NOT NULL REFERENCES usuario(id),
    fecha         VARCHAR(20)  NOT NULL DEFAULT CURRENT_DATE,
    estado        BOOLEAN      NOT NULL DEFAULT TRUE,
    UNIQUE(curso_id, estudiante_id)
);

-- ============================================================
-- CORTE
-- Actividad evaluativa de un curso con su porcentaje
-- La suma de porcentajes por curso debe ser 100%
-- ============================================================
CREATE TABLE corte (
    id          SERIAL PRIMARY KEY,
    curso_id    INTEGER      NOT NULL REFERENCES curso(id) ON DELETE CASCADE,
    porcentaje  DECIMAL(5,2) NOT NULL CHECK (porcentaje > 0 AND porcentaje <= 100)
);

-- ============================================================
-- NOTA
-- Calificacion de un estudiante en un corte
-- Valor entre 0.0 y 5.0
-- ============================================================
CREATE TABLE nota (
    id            SERIAL PRIMARY KEY,
    estudiante_id INTEGER      NOT NULL REFERENCES usuario(id),
    corte_id      INTEGER      NOT NULL REFERENCES corte(id) ON DELETE CASCADE,
    valor         DECIMAL(3,1) NOT NULL CHECK (valor >= 0.0 AND valor <= 5.0),
    UNIQUE(estudiante_id, corte_id)
);
