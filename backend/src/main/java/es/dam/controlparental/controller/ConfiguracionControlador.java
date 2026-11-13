package es.dam.controlparental.controller;

import es.dam.controlparental.dto.SolicitudTiempoUso;
import es.dam.controlparental.dto.RespuestaTiempoUso;

import es.dam.controlparental.dto.SolicitudOrigenPreguntas;
import es.dam.controlparental.dto.RespuestaOrigenPreguntas;

import es.dam.controlparental.dto.SolicitudModalidadCuestionario;
import es.dam.controlparental.dto.RespuestaModalidadCuestionario;

import es.dam.controlparental.service.ConfiguracionServicio;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tutores/{idTutor}/menores/{idMenor}/configuracion")
public class ConfiguracionControlador {

    private final ConfiguracionServicio configuracionServicio;

    public ConfiguracionControlador(
            ConfiguracionServicio configuracionServicio
    ) {
        this.configuracionServicio = configuracionServicio;
    }

    // ==========================================
    // TIEMPO DE USO
    // ==========================================

    @GetMapping("/tiempo-uso")
    public RespuestaTiempoUso obtenerTiempoUso(
            @PathVariable("idTutor") long idTutor,
            @PathVariable("idMenor") long idMenor
    ) {

        return configuracionServicio.obtenerTiempoUso(
                idTutor,
                idMenor
        );
    }

    @PutMapping("/tiempo-uso")
    public RespuestaTiempoUso guardarTiempoUso(
            @PathVariable("idTutor") long idTutor,
            @PathVariable("idMenor") long idMenor,
            @Valid @RequestBody SolicitudTiempoUso solicitud
    ) {

        return configuracionServicio.guardarTiempoUso(
                idTutor,
                idMenor,
                solicitud
        );
    }

    // ==========================================
    // ORIGEN DE LAS PREGUNTAS
    // ==========================================

    @GetMapping("/origen-preguntas")
    public RespuestaOrigenPreguntas obtenerOrigenPreguntas(
            @PathVariable("idTutor") long idTutor,
            @PathVariable("idMenor") long idMenor
    ) {

        return configuracionServicio.obtenerOrigenPreguntas(
                idTutor,
                idMenor
        );
    }

    @PutMapping("/origen-preguntas")
    public RespuestaOrigenPreguntas guardarOrigenPreguntas(
            @PathVariable("idTutor") long idTutor,
            @PathVariable("idMenor") long idMenor,
            @Valid @RequestBody SolicitudOrigenPreguntas solicitud
    ) {

        return configuracionServicio.guardarOrigenPreguntas(
                idTutor,
                idMenor,
                solicitud
        );
    }

    // ==========================================
    // MODALIDAD DEL CUESTIONARIO
    // ==========================================

    @GetMapping("/modalidad-cuestionario")
    public RespuestaModalidadCuestionario obtenerModalidad(
            @PathVariable("idTutor") long idTutor,
            @PathVariable("idMenor") long idMenor
    ) {

        return configuracionServicio.obtenerModalidad(
                idTutor,
                idMenor
        );
    }

    @PutMapping("/modalidad-cuestionario")
    public RespuestaModalidadCuestionario guardarModalidad(
            @PathVariable("idTutor") long idTutor,
            @PathVariable("idMenor") long idMenor,
            @Valid @RequestBody SolicitudModalidadCuestionario solicitud
    ) {

        return configuracionServicio.guardarModalidad(
                idTutor,
                idMenor,
                solicitud
        );
    }
}
