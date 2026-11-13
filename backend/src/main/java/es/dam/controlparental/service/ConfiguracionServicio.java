package es.dam.controlparental.service;

import es.dam.controlparental.dao.ConfiguracionDao;

import es.dam.controlparental.dto.SolicitudTiempoUso;
import es.dam.controlparental.dto.RespuestaTiempoUso;

import es.dam.controlparental.dto.SolicitudOrigenPreguntas;
import es.dam.controlparental.dto.RespuestaOrigenPreguntas;

import es.dam.controlparental.dto.SolicitudModalidadCuestionario;
import es.dam.controlparental.dto.RespuestaModalidadCuestionario;

import es.dam.controlparental.model.ConfiguracionTiempoUso;
import es.dam.controlparental.model.ConfiguracionModalidadCuestionario;
import es.dam.controlparental.model.OrigenPreguntas;
import es.dam.controlparental.model.ModoCuestionario;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ConfiguracionServicio {

    private final ConfiguracionDao configuracionDao;

    public ConfiguracionServicio(ConfiguracionDao configuracionDao) {
        this.configuracionDao = configuracionDao;
    }

    // ==========================================
    // 1. GESTIONAR TIEMPO DE USO
    // ==========================================

    @Transactional
    public RespuestaTiempoUso obtenerTiempoUso(
            long idTutor,
            long idMenor
    ) {

        validarIdentificadores(idTutor, idMenor);

        configuracionDao.asegurarConfiguracion(idTutor, idMenor);

        ConfiguracionTiempoUso configuracion =
                configuracionDao.buscarTiempoUso(idTutor, idMenor)
                        .orElseThrow(() -> menorNoEncontrado());

        return new RespuestaTiempoUso(
                configuracion.idMenor(),
                configuracion.tiempoSemana(),
                configuracion.tiempoFinde()
        );
    }

    @Transactional
    public RespuestaTiempoUso guardarTiempoUso(
            long idTutor,
            long idMenor,
            SolicitudTiempoUso solicitud
    ) {

        validarIdentificadores(idTutor, idMenor);

        configuracionDao.asegurarConfiguracion(idTutor, idMenor);

        int filasActualizadas = configuracionDao.actualizarTiempoUso(
                idTutor,
                idMenor,
                solicitud.tiempoSemana(),
                solicitud.tiempoFinde()
        );

        comprobarActualizacion(filasActualizadas);

        return new RespuestaTiempoUso(
                idMenor,
                solicitud.tiempoSemana(),
                solicitud.tiempoFinde()
        );
    }

    // ==========================================
    // 2. ELEGIR ORIGEN DE LAS PREGUNTAS
    // ==========================================

    @Transactional
    public RespuestaOrigenPreguntas obtenerOrigenPreguntas(
            long idTutor,
            long idMenor
    ) {

        validarIdentificadores(idTutor, idMenor);

        configuracionDao.asegurarConfiguracion(idTutor, idMenor);

        OrigenPreguntas origen = configuracionDao.buscarOrigenPreguntas(
                idTutor,
                idMenor
        ).orElseThrow(() -> menorNoEncontrado());

        return new RespuestaOrigenPreguntas(
                idMenor,
                origen
        );
    }

    @Transactional
    public RespuestaOrigenPreguntas guardarOrigenPreguntas(
            long idTutor,
            long idMenor,
            SolicitudOrigenPreguntas solicitud
    ) {

        validarIdentificadores(idTutor, idMenor);

        configuracionDao.asegurarConfiguracion(idTutor, idMenor);

        int filasActualizadas =
                configuracionDao.actualizarOrigenPreguntas(
                        idTutor,
                        idMenor,
                        solicitud.origen()
                );

        comprobarActualizacion(filasActualizadas);

        return new RespuestaOrigenPreguntas(
                idMenor,
                solicitud.origen()
        );
    }

    // ==========================================
    // 3. CONFIGURAR MODALIDAD DEL CUESTIONARIO
    // ==========================================

    @Transactional
    public RespuestaModalidadCuestionario obtenerModalidad(
            long idTutor,
            long idMenor
    ) {

        validarIdentificadores(idTutor, idMenor);

        configuracionDao.asegurarConfiguracion(idTutor, idMenor);

        ConfiguracionModalidadCuestionario configuracion =
                configuracionDao.buscarModalidad(idTutor, idMenor)
                        .orElseThrow(() -> menorNoEncontrado());

        return new RespuestaModalidadCuestionario(
                configuracion.idMenor(),
                configuracion.minutosAcierto(),
                configuracion.modoCuestionario()
        );
    }

    @Transactional
    public RespuestaModalidadCuestionario guardarModalidad(
            long idTutor,
            long idMenor,
            SolicitudModalidadCuestionario solicitud
    ) {

        validarIdentificadores(idTutor, idMenor);

        configuracionDao.asegurarConfiguracion(idTutor, idMenor);

        int filasActualizadas =
                configuracionDao.actualizarModalidad(
                        idTutor,
                        idMenor,
                        solicitud.minutosAcierto(),
                        solicitud.modoCuestionario()
                );

        comprobarActualizacion(filasActualizadas);

        return new RespuestaModalidadCuestionario(
                idMenor,
                solicitud.minutosAcierto(),
                solicitud.modoCuestionario()
        );
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private void validarIdentificadores(
            long idTutor,
            long idMenor
    ) {

        ValidadorIdentificadores.validarIdTutor(idTutor);
        ValidadorIdentificadores.validarIdMenor(idMenor);
    }

    private void comprobarActualizacion(int filasActualizadas) {

        if (filasActualizadas == 0) {
            throw menorNoEncontrado();
        }
    }

    private ResponseStatusException menorNoEncontrado() {

        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No se ha encontrado el menor o no pertenece al tutor"
        );
    }
}
