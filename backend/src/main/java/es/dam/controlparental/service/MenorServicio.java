package es.dam.controlparental.service;

import es.dam.controlparental.dao.MenorDao;
import es.dam.controlparental.dao.ConfiguracionDao;
import es.dam.controlparental.dto.SolicitudPerfilMenor;
import es.dam.controlparental.dto.RespuestaPerfilMenor;
import es.dam.controlparental.model.Menor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MenorServicio {

    private final MenorDao menorDao;
    private final ConfiguracionDao configuracionDao;

    public MenorServicio(
            MenorDao menorDao,
            ConfiguracionDao configuracionDao
    ) {
        this.menorDao = menorDao;
        this.configuracionDao = configuracionDao;
    }

    // Crear perfil del menor
    @Transactional
    public RespuestaPerfilMenor crearPerfil(
            long idTutor,
            SolicitudPerfilMenor solicitud
    ) {

        ValidadorIdentificadores.validarIdTutor(idTutor);

        Menor menor = menorDao.crear(
                idTutor,
                solicitud.nombre(),
                solicitud.edad()
        );

        // Crear la configuración inicial del menor
        configuracionDao.asegurarConfiguracion(
                idTutor,
                menor.idMenor()
        );

        return convertirRespuesta(menor);
    }

    // Consultar perfil del menor
    public RespuestaPerfilMenor obtenerPerfil(
            long idTutor,
            long idMenor
    ) {

        ValidadorIdentificadores.validarIdTutor(idTutor);
        ValidadorIdentificadores.validarIdMenor(idMenor);

        Menor menor = menorDao.buscarPorIdTutor(idTutor, idMenor)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se ha encontrado el menor"
                ));

        return convertirRespuesta(menor);
    }

    // Modificar perfil del menor
    @Transactional
    public RespuestaPerfilMenor actualizarPerfil(
            long idTutor,
            long idMenor,
            SolicitudPerfilMenor solicitud
    ) {

        ValidadorIdentificadores.validarIdTutor(idTutor);
        ValidadorIdentificadores.validarIdMenor(idMenor);

        int filasActualizadas = menorDao.actualizar(
                idTutor,
                idMenor,
                solicitud.nombre(),
                solicitud.edad()
        );

        if (filasActualizadas == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se ha encontrado el menor"
            );
        }

        return obtenerPerfil(idTutor, idMenor);
    }

    // Convertir modelo en DTO de respuesta
    private RespuestaPerfilMenor convertirRespuesta(Menor menor) {

        return new RespuestaPerfilMenor(
                menor.idMenor(),
                menor.idTutor(),
                menor.nombre(),
                menor.edad()
        );
    }
}