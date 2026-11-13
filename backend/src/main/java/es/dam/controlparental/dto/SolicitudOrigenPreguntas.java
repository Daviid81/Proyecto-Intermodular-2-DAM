package es.dam.controlparental.dto;

import es.dam.controlparental.model.OrigenPreguntas;
import jakarta.validation.constraints.NotNull;

public record SolicitudOrigenPreguntas(

        @NotNull(message = "El origen de las preguntas es obligatorio")
        OrigenPreguntas origen

) {}
