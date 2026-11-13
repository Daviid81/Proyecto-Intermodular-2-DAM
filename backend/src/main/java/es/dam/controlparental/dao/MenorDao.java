package es.dam.controlparental.dao;

import es.dam.controlparental.model.Menor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MenorDao {

    private final JdbcTemplate jdbcTemplate;

    public MenorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Crear un menor asociado a un tutor
    public Menor crear(long idTutor, String nombre, int edad) {

        Long idMenor = jdbcTemplate.queryForObject(
                """
                INSERT INTO menor (id_tutor, nombre, edad)
                VALUES (?, ?, ?)
                RETURNING id_menor
                """,
                Long.class,
                idTutor,
                nombre,
                edad
        );

        return new Menor(idMenor, idTutor, nombre, edad);
    }

    // Buscar un menor comprobando que pertenece al tutor
    public Optional<Menor> buscarPorIdTutor(
            long idTutor,
            long idMenor
    ) {

        return jdbcTemplate.query(
                """
                SELECT id_menor, id_tutor, nombre, edad
                FROM menor
                WHERE id_tutor = ?
                  AND id_menor = ?
                """,
                rs -> {
                    if (rs.next()) {
                        return Optional.of(new Menor(
                                rs.getLong("id_menor"),
                                rs.getLong("id_tutor"),
                                rs.getString("nombre"),
                                rs.getInt("edad")
                        ));
                    }

                    return Optional.empty();
                },
                idTutor,
                idMenor
        );
    }

    // Modificar el nombre y la edad de un menor
    public int actualizar(
            long idTutor,
            long idMenor,
            String nombre,
            int edad
    ) {

        return jdbcTemplate.update(
                """
                UPDATE menor
                SET nombre = ?, edad = ?
                WHERE id_tutor = ?
                  AND id_menor = ?
                """,
                nombre,
                edad,
                idTutor,
                idMenor
        );
    }
}