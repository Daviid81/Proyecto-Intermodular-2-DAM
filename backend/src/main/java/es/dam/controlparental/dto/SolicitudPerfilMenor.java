package es.dam.controlparental.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SolicitudPerfilMenor(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        String nombre,

        @NotNull(message = "La edad es obligatoria")
        @Min(value = 12, message = "La edad mínima es 12 años")
        @Max(value = 16, message = "La edad máxima es 16 años")
        Integer edad

) {}