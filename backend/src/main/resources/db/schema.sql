-- =====================================================================
--  Base de datos: App de control del uso del móvil (TFG 2.º DAM)
--  Motor: PostgreSQL
--  Convenciones: nombres en minúsculas y sin tildes ni ñ (contrasena),
--                tiempos en minutos, claves primarias autogeneradas.
-- =====================================================================

-- ---------------------------------------------------------------------
--  TUTOR  (el padre/madre; es quien inicia sesión)
-- ---------------------------------------------------------------------
CREATE TABLE tutor (
    id_tutor        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario         VARCHAR(50)  NOT NULL UNIQUE,
    email           VARCHAR(120) NOT NULL UNIQUE,
    contrasena      VARCHAR(255) NOT NULL,            -- hash (BCrypt), nunca en claro
    fecha_registro  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ---------------------------------------------------------------------
--  MENOR  (entidad débil: no existe sin su tutor)
--  La dependencia se expresa con id_tutor NOT NULL + ON DELETE CASCADE.
-- ---------------------------------------------------------------------
CREATE TABLE menor (
    id_menor        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_tutor        BIGINT       NOT NULL
                    REFERENCES tutor (id_tutor) ON DELETE CASCADE,
    nombre          VARCHAR(50)  NOT NULL,
    edad            SMALLINT     NOT NULL CHECK (edad BETWEEN 12 AND 16),
    id_dispositivo  VARCHAR(100) UNIQUE,              -- se rellena al vincular el móvil
    UNIQUE (id_tutor, nombre)
);
CREATE INDEX idx_menor_tutor ON menor (id_tutor);

-- ---------------------------------------------------------------------
--  CONFIGURACION  (1:1 con MENOR; todo lo fija el padre)
-- ---------------------------------------------------------------------
CREATE TABLE configuracion (
    id_config           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_menor            BIGINT   NOT NULL UNIQUE
                        REFERENCES menor (id_menor) ON DELETE CASCADE,
    tiempo_semana       INTEGER  NOT NULL DEFAULT 60  CHECK (tiempo_semana       >= 0),
    tiempo_finde        INTEGER  NOT NULL DEFAULT 120 CHECK (tiempo_finde        >= 0),
    max_ganados_semana  INTEGER  NOT NULL DEFAULT 30  CHECK (max_ganados_semana  >= 0),
    max_ganados_finde   INTEGER  NOT NULL DEFAULT 60  CHECK (max_ganados_finde   >= 0),
    minutos_acierto     INTEGER  NOT NULL DEFAULT 1   CHECK (minutos_acierto     >  0),
    modo_cuestionario   VARCHAR(15) NOT NULL DEFAULT 'LIBRE'
                        CHECK (modo_cuestionario IN ('LIBRE', 'BLOQUE_COMPLETO')),
    num_preguntas       INTEGER  NOT NULL DEFAULT 5   CHECK (num_preguntas       >  0),
    origen_preguntas    VARCHAR(10) NOT NULL DEFAULT 'MIXTAS'
                        CHECK (origen_preguntas IN ('IA', 'PROPIAS', 'MIXTAS'))
);

-- ---------------------------------------------------------------------
--  APLICACION  (apps instaladas en el móvil de ese menor)
-- ---------------------------------------------------------------------
CREATE TABLE aplicacion (
    id_aplicacion  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_menor       BIGINT       NOT NULL
                   REFERENCES menor (id_menor) ON DELETE CASCADE,
    nombre         VARCHAR(100) NOT NULL,             -- "Instagram"
    paquete        VARCHAR(255) NOT NULL,             -- "com.instagram.android"
    bloqueada      BOOLEAN      NOT NULL DEFAULT FALSE,
    UNIQUE (id_menor, paquete)
);
CREATE INDEX idx_aplicacion_menor ON aplicacion (id_menor);

-- ---------------------------------------------------------------------
--  REGISTRO_USO  (minutos usados por app y día; alimenta las estadísticas)
-- ---------------------------------------------------------------------
CREATE TABLE registro_uso (
    id_registro    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_aplicacion  BIGINT   NOT NULL
                   REFERENCES aplicacion (id_aplicacion) ON DELETE CASCADE,
    fecha          DATE     NOT NULL DEFAULT CURRENT_DATE,
    minutos_usados INTEGER  NOT NULL DEFAULT 0 CHECK (minutos_usados >= 0),
    UNIQUE (id_aplicacion, fecha)
);

-- ---------------------------------------------------------------------
--  REPOSITORIO  (carpetas de preguntas)
--    id_tutor NULL  -> repositorio IA (solo existe uno en todo el sistema)
--    id_tutor NOT NULL -> repositorio propio de un tutor (lengua, mates...)
--    predeterminado -> repositorio "default" de cada tutor
--  es_ia es una columna calculada que usa pregunta para comprobar que el
--  tipo de la pregunta coincide con el tipo de su repositorio.
-- ---------------------------------------------------------------------
CREATE TABLE repositorio (
    id_repositorio  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_tutor        BIGINT
                    REFERENCES tutor (id_tutor) ON DELETE CASCADE,
    nombre          VARCHAR(80) NOT NULL,
    descripcion     TEXT,
    predeterminado  BOOLEAN     NOT NULL DEFAULT FALSE,
    es_ia           BOOLEAN GENERATED ALWAYS AS (id_tutor IS NULL) STORED,
    UNIQUE (id_tutor, nombre),
    UNIQUE (id_repositorio, id_tutor),     -- para la FK compuesta de pregunta (mismo tutor)
    UNIQUE (id_repositorio, es_ia),        -- para la FK compuesta de pregunta (mismo tipo)
    CHECK (NOT (predeterminado AND id_tutor IS NULL))
);
-- Un solo repositorio por defecto por tutor
CREATE UNIQUE INDEX uq_repositorio_predeterminado
    ON repositorio (id_tutor) WHERE predeterminado;
-- Un solo repositorio IA en todo el sistema
CREATE UNIQUE INDEX uq_repositorio_ia
    ON repositorio ((TRUE)) WHERE id_tutor IS NULL;

-- ---------------------------------------------------------------------
--  PREGUNTA  (cada pregunta vive en un unico repositorio: contiene 1:N)
--    tipo GENERAL -> la genera la IA: sin tutor, en el repositorio IA, con edad_objetivo
--    tipo PROPIA  -> la redacta un tutor (redacta) y esta en un repositorio de ese mismo tutor
--
--  Restricciones declarativas:
--   * (id_repositorio, id_tutor) -> el repositorio de una pregunta PROPIA es del tutor que la redacto.
--   * (id_repositorio, es_ia)    -> una pregunta GENERAL solo en el repositorio IA y una PROPIA nunca en el.
--  Borrar un repositorio borra sus preguntas.
-- ---------------------------------------------------------------------
CREATE TABLE pregunta (
    id_pregunta     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_repositorio  BIGINT      NOT NULL,
    id_tutor        BIGINT
                    REFERENCES tutor (id_tutor) ON DELETE CASCADE,
    tipo            VARCHAR(10) NOT NULL CHECK (tipo IN ('GENERAL', 'PROPIA')),
    enunciado       TEXT        NOT NULL,
    justificacion   TEXT        NOT NULL,
    edad_objetivo   SMALLINT    CHECK (edad_objetivo BETWEEN 12 AND 16),
    fecha_creacion  TIMESTAMP   NOT NULL DEFAULT NOW(),
    repo_es_ia      BOOLEAN GENERATED ALWAYS AS (tipo = 'GENERAL') STORED,
    FOREIGN KEY (id_repositorio, id_tutor)
        REFERENCES repositorio (id_repositorio, id_tutor) ON DELETE CASCADE,
    FOREIGN KEY (id_repositorio, repo_es_ia)
        REFERENCES repositorio (id_repositorio, es_ia) ON DELETE CASCADE,
    CHECK (
        (tipo = 'GENERAL' AND id_tutor IS NULL     AND edad_objetivo IS NOT NULL)
     OR (tipo = 'PROPIA'  AND id_tutor IS NOT NULL AND edad_objetivo IS NULL)
    )
);
CREATE INDEX idx_pregunta_repositorio ON pregunta (id_repositorio);
CREATE INDEX idx_pregunta_tutor       ON pregunta (id_tutor);

-- ---------------------------------------------------------------------
--  OPCION  (respuestas posibles de una pregunta; es_correcta marca las válidas)
-- ---------------------------------------------------------------------
CREATE TABLE opcion (
    id_opcion    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_pregunta  BIGINT       NOT NULL
                 REFERENCES pregunta (id_pregunta) ON DELETE CASCADE,
    texto        VARCHAR(255) NOT NULL,
    es_correcta  BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_opcion_pregunta ON opcion (id_pregunta);

-- ---------------------------------------------------------------------
--  CUESTIONARIO  (cada vez que el menor responde preguntas para ganar tiempo)
--    fecha_fin NULL mientras el cuestionario sigue en curso.
-- ---------------------------------------------------------------------
CREATE TABLE cuestionario (
    id_cuestionario  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_menor         BIGINT    NOT NULL
                     REFERENCES menor (id_menor) ON DELETE CASCADE,
    fecha_inicio     TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_fin        TIMESTAMP,
    aciertos         INTEGER   NOT NULL DEFAULT 0 CHECK (aciertos >= 0),
    minutos_ganados  INTEGER   NOT NULL DEFAULT 0 CHECK (minutos_ganados >= 0),
    CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio)
);
CREATE INDEX idx_cuestionario_menor ON cuestionario (id_menor);

-- ---------------------------------------------------------------------
--  CUESTIONARIO_PREGUNTA  (N:M "incluye")
--    acertada NULL hasta que se corrige el cuestionario.
--    Sirve también para no repetir preguntas ya mostradas a ese menor.
-- ---------------------------------------------------------------------
CREATE TABLE cuestionario_pregunta (
    id_cuestionario  BIGINT NOT NULL
                     REFERENCES cuestionario (id_cuestionario) ON DELETE CASCADE,
    id_pregunta      BIGINT NOT NULL
                     REFERENCES pregunta (id_pregunta) ON DELETE CASCADE,
    acertada         BOOLEAN,
    PRIMARY KEY (id_cuestionario, id_pregunta)
);
CREATE INDEX idx_cuestpreg_pregunta ON cuestionario_pregunta (id_pregunta);

-- =====================================================================
--  AUTOMATISMOS
-- =====================================================================

-- Repositorio IA unico (se crea al montar la base de datos)
INSERT INTO repositorio (id_tutor, nombre, descripcion)
VALUES (NULL, 'IA', 'Preguntas de cultura general generadas por la IA');

-- Al registrarse un tutor se le crea su repositorio "default"
CREATE FUNCTION crear_repositorio_default() RETURNS trigger AS $$
BEGIN
    INSERT INTO repositorio (id_tutor, nombre, descripcion, predeterminado)
    VALUES (NEW.id_tutor, 'default', 'Preguntas sin repositorio asignado', TRUE);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_tutor_repositorio_default
    AFTER INSERT ON tutor
    FOR EACH ROW EXECUTE FUNCTION crear_repositorio_default();

-- El repositorio por defecto y el de IA no se pueden renombrar ni modificar
CREATE FUNCTION proteger_repositorio_especial() RETURNS trigger AS $$
BEGIN
    IF (OLD.predeterminado OR OLD.id_tutor IS NULL)
       AND (NEW.nombre <> OLD.nombre OR NEW.predeterminado <> OLD.predeterminado
            OR NEW.id_tutor IS DISTINCT FROM OLD.id_tutor) THEN
        RAISE EXCEPTION 'El repositorio por defecto y el de IA no se pueden renombrar ni modificar';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_repositorio_proteger_update
    BEFORE UPDATE ON repositorio
    FOR EACH ROW EXECUTE FUNCTION proteger_repositorio_especial();

-- El repositorio por defecto y el de IA no se pueden borrar
-- (salvo que se borre el tutor entero: entonces ya no existe su fila)
CREATE FUNCTION proteger_repositorio_borrado() RETURNS trigger AS $$
BEGIN
    IF OLD.id_tutor IS NULL THEN
        RAISE EXCEPTION 'El repositorio IA no se puede borrar';
    ELSIF OLD.predeterminado AND EXISTS (SELECT 1 FROM tutor WHERE id_tutor = OLD.id_tutor) THEN
        RAISE EXCEPTION 'El repositorio por defecto no se puede borrar';
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_repositorio_proteger_delete
    BEFORE DELETE ON repositorio
    FOR EACH ROW EXECUTE FUNCTION proteger_repositorio_borrado();
