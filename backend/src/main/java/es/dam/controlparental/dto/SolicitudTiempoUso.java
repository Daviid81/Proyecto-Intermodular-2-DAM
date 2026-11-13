package es.dam.controlparental.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SolicitudTiempoUso(

        @NotNull(message = "El tiempo entre semana es obligatorio")
        @PositiveOrZero(message = "El tiempo entre semana no puede ser negativo")
        Integer tiempoSemana,

        @NotNull(message = "El tiempo del fin de semana es obligatorio")
        @PositiveOrZero(message = "El tiempo del fin de semana no puede ser negativo")
        Integer tiempoFinde

) {}
