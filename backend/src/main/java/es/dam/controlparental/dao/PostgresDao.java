package es.dam.controlparental.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresDao {
    private final JdbcTemplate jdbc;

    public PostgresDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void comprobarConexion() {
        jdbc.queryForObject("SELECT 1", Integer.class);
    }
}
