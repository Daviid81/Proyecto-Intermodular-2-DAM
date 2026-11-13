package es.dam.controlparental.dao;

import es.dam.controlparental.model.ConfiguracionModalidadCuestionario;
import es.dam.controlparental.model.ConfiguracionTiempoUso;
import es.dam.controlparental.model.ModoCuestionario;
import es.dam.controlparental.model.OrigenPreguntas;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ConfiguracionDao {

    private final JdbcTemplate jdbcTemplate;

    public ConfiguracionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Crear configuración con los valores por defecto de PostgreSQL.
    // Si ya existe, no modifica los datos.
    // Comprueba también que el menor pertenece al tutor.
    public void asegurarConfiguracion(long idTutor, long idMenor) {

        jdbcTemplate.update(
                """
                INSERT INTO configuracion (id_menor)
                SELECT id_menor
                FROM menor
                WHERE id_menor = ?
                  AND id_tutor = ?
                ON CONFLICT (id_menor) DO NOTHING
                """,
                idMenor,
                idTutor
        );
    }

    // ==========================================
    // TIEMPO DE USO
    // ==========================================

    public Optional<ConfiguracionTiempoUso> buscarTiempoUso(
            long idTutor,
            long idMenor
    ) {

        return jdbcTemplate.query(
                """
                SELECT c.id_menor,
                       c.tiempo_semana,
                       c.tiempo_finde
                FROM configuracion c
                INNER JOIN menor m ON c.id_menor = m.id_menor
                WHERE m.id_tutor = ?
                  AND m.id_menor = ?
                """,
                rs -> {
                    if (rs.next()) {
                        return Optional.of(new ConfiguracionTiempoUso(
                                rs.getLong("id_menor"),
                                rs.getInt("tiempo_semana"),
                                rs.getInt("tiempo_finde")
                        ));
                    }

                    return Optional.empty();
                },
                idTutor,
                idMenor
        );
    }

    public int actualizarTiempoUso(
            long idTutor,
            long idMenor,
            int tiempoSemana,
            int tiempoFinde
    ) {

        return jdbcTemplate.update(
                """
                UPDATE configuracion c
                SET tiempo_semana = ?,
                    tiempo_finde = ?
                FROM menor m
                WHERE c.id_menor = m.id_menor
                  AND m.id_tutor = ?
                  AND m.id_menor = ?
                """,
                tiempoSemana,
                tiempoFinde,
                idTutor,
                idMenor
        );
    }

    // ==========================================
    // ORIGEN DE LAS PREGUNTAS
    // ==========================================

    public Optional<OrigenPreguntas> buscarOrigenPreguntas(
            long idTutor,
            long idMenor
    ) {

        return jdbcTemplate.query(
                """
                SELECT c.origen_preguntas
                FROM configuracion c
                INNER JOIN menor m ON c.id_menor = m.id_menor
                WHERE m.id_tutor = ?
                  AND m.id_menor = ?
                """,
                rs -> {
                    if (rs.next()) {
                        return Optional.of(
                                OrigenPreguntas.valueOf(
                                        rs.getString("origen_preguntas")
                                )
                        );
                    }

                    return Optional.empty();
                },
                idTutor,
                idMenor
        );
    }

    public int actualizarOrigenPreguntas(
            long idTutor,
            long idMenor,
            OrigenPreguntas origen
    ) {

        return jdbcTemplate.update(
                """
                UPDATE configuracion c
                SET origen_preguntas = ?
                FROM menor m
                WHERE c.id_menor = m.id_menor
                  AND m.id_tutor = ?
                  AND m.id_menor = ?
                """,
                origen.name(),
                idTutor,
                idMenor
        );
    }

    // ==========================================
    // MODALIDAD DEL CUESTIONARIO
    // ==========================================

    public Optional<ConfiguracionModalidadCuestionario> buscarModalidad(
            long idTutor,
            long idMenor
    ) {

        return jdbcTemplate.query(
                """
                SELECT c.id_menor,
                       c.minutos_acierto,
                       c.modo_cuestionario
                FROM configuracion c
                INNER JOIN menor m ON c.id_menor = m.id_menor
                WHERE m.id_tutor = ?
                  AND m.id_menor = ?
                """,
                rs -> {
                    if (rs.next()) {
                        return Optional.of(
                                new ConfiguracionModalidadCuestionario(
                                        rs.getLong("id_menor"),
                                        rs.getInt("minutos_acierto"),
                                        ModoCuestionario.valueOf(
                                                rs.getString("modo_cuestionario")
                                        )
                                )
                        );
                    }

                    return Optional.empty();
                },
                idTutor,
                idMenor
        );
    }

    public int actualizarModalidad(
            long idTutor,
            long idMenor,
            int minutosAcierto,
            ModoCuestionario modoCuestionario
    ) {

        return jdbcTemplate.update(
                """
                UPDATE configuracion c
                SET minutos_acierto = ?,
                    modo_cuestionario = ?
                FROM menor m
                WHERE c.id_menor = m.id_menor
                  AND m.id_tutor = ?
                  AND m.id_menor = ?
                """,
                minutosAcierto,
                modoCuestionario.name(),
                idTutor,
                idMenor
        );
    }
}