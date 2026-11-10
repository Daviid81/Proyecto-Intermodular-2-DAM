package es.dam.controlparental.service;

import es.dam.controlparental.dao.PostgresDao;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class EstadoBackendService {
    private final PostgresDao postgresDao;

    public EstadoBackendService(PostgresDao postgresDao) {
        this.postgresDao = postgresDao;
    }

    public boolean postgresDisponible() {
        try {
            postgresDao.comprobarConexion();
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }
}
