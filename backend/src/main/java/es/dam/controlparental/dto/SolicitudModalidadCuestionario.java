package es.dam.controlparental.dto;

import es.dam.controlparental.model.ModoCuestionario;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SolicitudModalidadCuestionario(

        @NotNull(message = "Los minutos por acierto son obligatorios")
        @Positive(message = "Los minutos por acierto deben ser positivos")
        Integer minutosAcierto,

        @NotNull(message = "El modo del cuestionario es obligatorio")
        ModoCuestionario modoCuestionario

) {}
