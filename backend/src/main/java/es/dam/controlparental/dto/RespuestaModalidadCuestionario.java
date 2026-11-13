package es.dam.controlparental.dto;

import es.dam.controlparental.model.ModoCuestionario;

public record RespuestaModalidadCuestionario(
        long idMenor,
        int minutosAcierto,
        ModoCuestionario modoCuestionario
) {}
