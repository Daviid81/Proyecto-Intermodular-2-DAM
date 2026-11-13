package es.dam.controlparental.controller;

import es.dam.controlparental.dto.SolicitudPerfilMenor;
import es.dam.controlparental.dto.RespuestaPerfilMenor;
import es.dam.controlparental.service.MenorServicio;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tutores/{idTutor}/menores")
public class MenorControlador {

    private final MenorServicio menorServicio;

    public MenorControlador(MenorServicio menorServicio) {
        this.menorServicio = menorServicio;
    }

    // Crear perfil del menor
    @PostMapping
    public ResponseEntity<RespuestaPerfilMenor> crearPerfil(
            @PathVariable("idTutor") long idTutor,
            @Valid @RequestBody SolicitudPerfilMenor solicitud
    ) {

        RespuestaPerfilMenor respuesta =
                menorServicio.crearPerfil(idTutor, solicitud);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    // Consultar perfil del menor
    @GetMapping("/{idMenor}")
    public RespuestaPerfilMenor obtenerPerfil(
            @PathVariable("idTutor") long idTutor,
            @PathVariable("idMenor") long idMenor
    ) {

        return menorServicio.obtenerPerfil(idTutor, idMenor);
    }

    // Modificar perfil del menor
    @PutMapping("/{idMenor}")
    public RespuestaPerfilMenor actualizarPerfil(
            @PathVariable("idTutor") long idTutor,
            @PathVariable("idMenor") long idMenor,
            @Valid @RequestBody SolicitudPerfilMenor solicitud
    ) {

        return menorServicio.actualizarPerfil(
                idTutor,
                idMenor,
                solicitud
        );
    }
}
